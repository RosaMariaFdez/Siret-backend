package dqteam.siret.service;

import dqteam.siret.dao.InvitationDAO;
import dqteam.siret.dao.OrganizationDAO;
import dqteam.siret.dao.UserDAO;
import dqteam.siret.dao.UserOrganizationDAO;
import dqteam.siret.model.Invitation;
import dqteam.siret.model.InvitationDTO;
import dqteam.siret.model.Organization;
import dqteam.siret.model.User;
import dqteam.siret.model.UserOrganization;
import dqteam.siret.model.UserOrganizationId;
import dqteam.siret.security.TokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvitationService {

    @Autowired
    private InvitationDAO invitationDAO;

    @Autowired
    private OrganizationDAO organizationDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailService emailService;
    
    @Autowired
	private UserOrganizationDAO userOrganizationDAO;

    public void sendInvitation(UUID organizationId, String email, String requesterEmail) {
        // Verificar si el requester tiene permisos (admin o jefe)
        Organization org = organizationDAO.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organización no encontrada"));

        User requester = userDAO.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario solicitante no encontrado"));

        if (!requester.isAdmin() && !org.getBoss().getId().equals(requester.getId())) {
            throw new SecurityException("No tienes permisos para invitar a esta organización");
        }
        
        // Verificar si el email ya está invitado
        if (invitationDAO.findByEmailAndAcceptedFalse(email).isPresent()) {
			throw new IllegalArgumentException("Ya existe una invitación pendiente para este email");
		}

        // Generar token JWT
        String token = tokenService.generateInvitationToken(email, organizationId.toString());
        // Crear la invitación
        Invitation invitation = new Invitation();
        invitation.setEmail(email);
        invitation.setOrganization(org);
        invitation.setToken(token);
        invitation.setSentAt(LocalDateTime.now());
        invitation.setExpiresAt(LocalDateTime.now().plusDays(2)); // Caduca en 2 días
        invitation.setAccepted(false);

        invitationDAO.save(invitation);

        Optional<User> invitedUser = userDAO.findByEmail(email);

        if (invitedUser.isPresent()) {
            emailService.sendInvitationEmailUsuario(email, token, org.getName());
        } else {
            // Usuario no registrado: enviar email con enlace de registro
            emailService.sendInvitationEmailNuevoUsuario(requesterEmail, token);

        }
    }
    
    //aceptar una invitación
    public void acceptInvitation(String token, String userEmail) {
        Invitation invitation = invitationDAO.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invitación no válida"));

        if (invitation.isAccepted()) {
            throw new IllegalStateException("La invitación ya fue aceptada");
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("La invitación ha expirado");
        }

        if (!invitation.getEmail().equalsIgnoreCase(userEmail)) {
            throw new SecurityException("No puedes aceptar invitaciones de otro usuario");
        }

        User user = userDAO.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no registrado"));

        Organization org = invitation.getOrganization();

        // Comprobar si ya pertenece
        if (userOrganizationDAO.existsByUserIdAndOrganizationId(user.getId(), org.getId())) {
            throw new IllegalStateException("Ya perteneces a esta organización");
        }

        // Asociar
        addUserToOrganization(user, org);

        // Marcar como aceptada
        invitation.setAccepted(true);
        invitationDAO.save(invitation);
    }
    
    //añadir usuario a organización
    private void addUserToOrganization(User user, Organization org) {
        UserOrganization relation = new UserOrganization();
        relation.setId(new UserOrganizationId(user.getId(), org.getId()));
        relation.setUser(user);
        relation.setOrganization(org);
        relation.setJoinedAt(java.time.LocalDateTime.now());
        userOrganizationDAO.save(relation);
    }

    
    //rechazar invitación
    public void rejectInvitation(String token, String userEmail) {
        Invitation invitation = invitationDAO.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invitación no válida"));

        if (invitation.isAccepted()) {
            throw new IllegalStateException("La invitación ya fue aceptada");
        }

        if (!invitation.getEmail().equalsIgnoreCase(userEmail)) {
            throw new SecurityException("No puedes rechazar invitaciones de otro usuario");
        }

        // Borrar la invitacion
        invitationDAO.delete(invitation);
        
    }

    //invitaciones pendientes de un usuario
    public List<InvitationDTO> getPendingInvitations(String userEmail) {
        Optional<Invitation> pending = invitationDAO.findByEmailAndAcceptedFalse(userEmail);

        return pending.stream()
                .filter(i -> i.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(i -> new InvitationDTO(
                        i.getId(),
                        i.getOrganization().getId(),
                        i.getOrganization().getName(),
                        i.getEmail(),
                        i.getToken(),
                        i.getSentAt(),
                        i.getExpiresAt()
                ))
                .toList();
    }

    

}