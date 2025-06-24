package dqteam.siret.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dqteam.siret.model.Organization;
import dqteam.siret.model.User;
import dqteam.siret.model.UserOrganization;
import dqteam.siret.model.UserOrganizationId;

public interface UserOrganizationDAO extends JpaRepository<UserOrganization, UserOrganizationId> {

	//buscar organizaciones por usuarios
	List<UserOrganization> findById_UserId(UUID userId);
	
	//existe ya usuario en organización
	boolean existsById_UserIdAndId_OrganizationId(UUID userId, UUID organizationId);


	
	

}
