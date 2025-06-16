package dqteam.siret.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import dqteam.siret.model.PasswordResetToken;


public interface PasswordResetTokenDAO extends JpaRepository<PasswordResetToken, String> {}
