package dqteam.siret.dao;

import dqteam.siret.model.Organization;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationDAO extends JpaRepository<Organization, UUID> {

}
