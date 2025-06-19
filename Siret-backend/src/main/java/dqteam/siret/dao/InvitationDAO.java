package dqteam.siret.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dqteam.siret.model.Invitation;
import dqteam.siret.model.Organization;

public interface InvitationDAO extends JpaRepository<Invitation, UUID> {

	List<Invitation> findByEmail(String email);
	
	List<Invitation> findByOrganization(Organization organization);
	
	Optional<Invitation> findByToken(String token);
	
	Optional<Invitation> findByEmailAndAcceptedFalse(String email);

}
