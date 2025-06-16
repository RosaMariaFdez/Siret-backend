package dqteam.siret.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dqteam.siret.model.UserOrganization;
import dqteam.siret.model.UserOrganizationId;

public interface UserOrganizationDAO extends JpaRepository<UserOrganization, UserOrganizationId> {

	List<UserOrganization> findByUserOrgs(UUID userId);

}
