package dqteam.siret.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dqteam.siret.dao.UserDAO;
import dqteam.siret.dao.UserOrganizationDAO;
import dqteam.siret.model.OrganizationSimpleDTO;
import dqteam.siret.model.User;
import dqteam.siret.model.UserDTO;
import dqteam.siret.model.UserOrganization;

@Service
public class AdminService {

    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private UserOrganizationDAO userOrganizationDAO;
    
    //dar permisos
    public void grantAdminRole(String email) {
        User user = userDAO.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        user.setAdmin(true);
        userDAO.save(user);
    }
    
    public void grantBossRole(String email) {
		User user = userDAO.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
		user.setBoss(true);
		userDAO.save(user);
	}
    
    //quitar permisos
    public void revokeAdminRole(String email) {
        User user = userDAO.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        user.setAdmin(false);
        userDAO.save(user);
    }
    
    public void revokeBossRole(String email) {
    	User user = userDAO.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    	user.setBoss(false);
		userDAO.save(user);
    }

    public List<UserDTO> getAdmins() {
        return userDAO.findByIsAdminTrue().stream()
            .map(user -> new UserDTO(user.getId(), user.getName(), user.getSurname(), user.getEmail(), user.isAdmin(), user.isBoss()))
            .toList();
    }
    
    //eliminar usuario completamente
    public void deleteUser(String email) {
		User user = userDAO.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
		userDAO.delete(user);
	}
    
    // Obtener organizaciones de un usuario por email
    public List<OrganizationSimpleDTO> getOrganizationsOfUser(UUID userId) {
        List<UserOrganization> relations = userOrganizationDAO.findByUserOrgs(userId);

        return relations.stream()
                .map(rel -> new OrganizationSimpleDTO(
                        rel.getOrganization().getId(),
                        rel.getOrganization().getName(),
                		rel.getOrganization().getDescription()))
                .collect(Collectors.toList());
    }

}

