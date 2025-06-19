package dqteam.siret.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dqteam.siret.dao.OrganizationDAO;
import dqteam.siret.dao.UserDAO;
import dqteam.siret.dao.UserOrganizationDAO;
import dqteam.siret.model.Organization;
import dqteam.siret.model.OrganizationDTO;
import dqteam.siret.model.User;
import dqteam.siret.model.UserOrganization;
import dqteam.siret.model.UserOrganizationId;

@Service
public class OrganizationService {

	@Autowired
	private OrganizationDAO organizationDAO;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private UserOrganizationDAO userOrganizationDAO;

	/*
	 * public Organization createOrganization(OrganizationDTO dto) { // Buscar al
	 * jefe por email Optional<User> optionalBoss =
	 * userDAO.findByEmail(dto.getBossEmail());
	 * 
	 * mirar si esto hace falta, se coge de la sesión del front if
	 * (optionalBoss.isEmpty()) { throw new
	 * IllegalArgumentException("No se encontró un usuario con ese email"); }
	 * 
	 * User boss = optionalBoss.get();
	 * 
	 * if (!boss.isBoss()) { throw new
	 * IllegalArgumentException("Este usuario no tiene permisos para crear organizaciones, contacte con un administrador"
	 * ); }
	 * 
	 * // Crear organización Organization organization = new Organization(
	 * dto.getName(), dto.getDescription(), dto.getWorklogType(), boss );
	 * 
	 * organization = organizationDAO.save(organization);
	 * 
	 * //-----------------------------------------------------------esto sí o esto
	 * no? // Añadir al jefe como miembro UserOrganization relation = new
	 * UserOrganization(); relation.setId(new UserOrganizationId(boss.getId(),
	 * organization.getId())); relation.setUser(boss);
	 * relation.setOrganization(organization);
	 * relation.setJoinedAt(java.time.LocalDateTime.now());
	 * 
	 * userOrganizationDAO.save(relation);
	 * //----------------------------------------------------------
	 * 
	 * return organization; }
	 */
	
	public Organization createOrganization(OrganizationDTO dto) {
	    // Obtener el usuario autenticado
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String loggedEmail = auth.getName(); // email del usuario logueado

	    // Verificar que el email autenticado sea el mismo que el bossEmail
	    if (!loggedEmail.equalsIgnoreCase(dto.getBossEmail())) {
	        throw new AccessDeniedException("No puedes crear una organización a nombre de otro usuario");
	    }

	    // Buscar al jefe por email
	    Optional<User> optionalBoss = userDAO.findByEmail(dto.getBossEmail());
	    if (optionalBoss.isEmpty()) {
	        throw new IllegalArgumentException("No se encontró un usuario con ese email");
	    }

	    User boss = optionalBoss.get();

	    // Verificar permisos del usuario (debe ser jefe o admin)
	    if (!boss.isBoss() && !boss.isAdmin()) {
	        throw new AccessDeniedException("Este usuario no tiene permisos para crear organizaciones");
	    }

	    // Crear organización
	    Organization organization = new Organization(
	            dto.getName(),
	            dto.getDescription(),
	            dto.getWorklogType(),
	            boss
	    );

	    organization = organizationDAO.save(organization);

	    // Añadir al jefe como miembro
	    UserOrganization relation = new UserOrganization();
	    relation.setId(new UserOrganizationId(boss.getId(), organization.getId()));
	    relation.setUser(boss);
	    relation.setOrganization(organization);
	    relation.setJoinedAt(java.time.LocalDateTime.now());

	    userOrganizationDAO.save(relation);

	    return organization;
	}

	//modificar organización
	public Organization updateOrganization(UUID organizationId, OrganizationDTO dto, String requesterEmail) {
	    Optional<User> optionalRequester = userDAO.findByEmail(requesterEmail);
	    if (optionalRequester.isEmpty()) {
	        throw new IllegalArgumentException("Usuario solicitante no encontrado");
	    }
	    User requester = optionalRequester.get();

	    Optional<Organization> optionalOrg = organizationDAO.findById(organizationId);
	    if (optionalOrg.isEmpty()) {
	        throw new IllegalArgumentException("Organización no encontrada");
	    }
	    Organization organization = optionalOrg.get();

	    // Solo admin o jefe actual pueden modificar
	    if (!requester.isAdmin() && !organization.getBoss().getId().equals(requester.getId())) {
	        throw new IllegalArgumentException("No tienes permisos para modificar esta organización");
	    }

	    // Actualizamos campos (excepto jefe que se cambia con otro método)
	    organization.setName(dto.getName());
	    organization.setDescription(dto.getDescription());
	    organization.setWorklogType(dto.getWorklogType());

	    return organizationDAO.save(organization);
	}

	//eliminar organización
	public void deleteOrganization(UUID organizationId, String requesterEmail) {
	    Optional<User> optionalRequester = userDAO.findByEmail(requesterEmail);
	    if (optionalRequester.isEmpty()) {
	        throw new IllegalArgumentException("Usuario solicitante no encontrado");
	    }
	    User requester = optionalRequester.get();

	    Optional<Organization> optionalOrg = organizationDAO.findById(organizationId);
	    if (optionalOrg.isEmpty()) {
	        throw new IllegalArgumentException("Organización no encontrada");
	    }
	    Organization organization = optionalOrg.get();

	    // Solo admin o jefe actual pueden eliminar
	    if (!requester.isAdmin() && !organization.getBoss().getId().equals(requester.getId())) {
	        throw new IllegalArgumentException("No tienes permisos para eliminar esta organización");
	    }

	    organizationDAO.delete(organization);
	}

	//cambiar jefe
	public Organization changeBoss(UUID organizationId, String newBossEmail, String requesterEmail) {
	    Optional<User> optionalRequester = userDAO.findByEmail(requesterEmail);
	    if (optionalRequester.isEmpty()) {
	        throw new IllegalArgumentException("Usuario solicitante no encontrado");
	    }
	    User requester = optionalRequester.get();

	    Optional<Organization> optionalOrg = organizationDAO.findById(organizationId);
	    if (optionalOrg.isEmpty()) {
	        throw new IllegalArgumentException("Organización no encontrada");
	    }
	    Organization organization = optionalOrg.get();

	    // Solo admin o jefe actual pueden cambiar jefe
	    if (!requester.isAdmin() && !organization.getBoss().getId().equals(requester.getId())) {
	        throw new IllegalArgumentException("No tienes permisos para cambiar el jefe");
	    }

	    Optional<User> optionalNewBoss = userDAO.findByEmail(newBossEmail);
	    if (optionalNewBoss.isEmpty()) {
	        throw new IllegalArgumentException("El nuevo jefe no existe");
	    }
	    User newBoss = optionalNewBoss.get();

	    newBoss.setBoss(true); //---------------------------------------------------------------------esto es para hacerlo más fácil
//	    if (!newBoss.isBoss()) {
//	        throw new IllegalArgumentException("El nuevo jefe no tiene permiso de jefe");
//	    }

	    organization.setBoss(newBoss);
	    return organizationDAO.save(organization);
	}

	/*	EN USER SERVICE
	 * //get my organizations info public List<OrganizationDTO>
	 * getMyOrganizations(String requesterEmail) { Optional<User> optionalRequester
	 * = userDAO.findByEmail(requesterEmail); if (optionalRequester.isEmpty()) {
	 * throw new IllegalArgumentException("Usuario solicitante no encontrado"); }
	 * User requester = optionalRequester.get();
	 * 
	 * // Obtener todas las organizaciones donde el usuario ya está
	 * List<UserOrganization> userOrganizations =
	 * userOrganizationDAO.findByUserOrgs(requester.getId());
	 * 
	 * // Mapear a DTO return userOrganizations.stream() .map(uo -> { Organization
	 * org = uo.getOrganization(); return new OrganizationDTO(org.getId(),
	 * org.getName(), org.getDescription(), org.getWorklogType(),
	 * org.getBoss().getEmail()); }) .collect(Collectors.toList()); }
	 */
	
	//get all organizations info
	public List<OrganizationDTO> getAllOrganizations() {
		 List<Organization> organizations = organizationDAO.findAll();
		    
		    // Mapear a DTO
		    return organizations.stream()
		            .map(org -> new OrganizationDTO(org.getId(), org.getName(), org.getDescription(), org.getWorklogType(), org.getBoss().getEmail()))
		            .collect(Collectors.toList());
	}
	   

}
