package dqteam.siret.dao;

import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import dqteam.siret.model.User;

public interface UserDAO extends JpaRepository<User, UUID> {
	
	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);
	Optional<User> findByEmailAndPassword(String email, String password);


}
