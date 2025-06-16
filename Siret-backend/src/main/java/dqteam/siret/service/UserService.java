package dqteam.siret.service;

import java.util.Optional;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import dqteam.siret.dao.UserDAO;
import dqteam.siret.model.User;
import dqteam.siret.model.UserPreferences;
import jakarta.servlet.http.HttpSession;

@Service
public class UserService {
	private static final int MAX_ATTEMPTS = 3;
	private static final long BLOCK_TIME = (15 * 60 * 1000L); // 15 minutos de bloqueo

	private final UserDAO userDAO;
	private final BCryptPasswordEncoder passwordEncoder;

	private ConcurrentHashMap<String, Integer> loginAttempts = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, Long> blockedSessions = new ConcurrentHashMap<>();

	public UserService(UserDAO userDAO, BCryptPasswordEncoder passwordEncoder) {
		this.userDAO = userDAO;
		this.passwordEncoder = passwordEncoder;
	}

	public User registerUser(User user) {
		if (userDAO.existsByEmail(user.getEmail())) {
			throw new IllegalArgumentException("Este email ya está en uso");
		}

		// user.setUserId(UUID.randomUUID());
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		UserPreferences preferences = new UserPreferences();
		preferences.setUser(user); // Establece la relación
		user.setUserPreferences(preferences); // Establece la relación inversa

		return userDAO.save(user);
	}

	
	public User login(User user, HttpSession session) {

		String sessionId = session.getId();
		if (isSessionBlocked(sessionId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"Usuario bloqueado temporalmente. Inténtelo más tarde.");
		}

		String email = user.getEmail();
		String password = user.getPassword();

		if (!emailFormatoValido(user)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato del email incorrecto");

		}
		
		Optional<User> userdb = userDAO.findByEmail(email);

        if(!userdb.isPresent()){
            throw new UsernameNotFoundException("User does not exist in the database");
        }
      
        if (!passwordEncoder.matches(password, userdb.get().getPassword())) {
        	incrementAttempts(sessionId);
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos");
        }
        
        return userdb.get();
	}

	// Bloquear sesión por intentos fallidos
	private boolean isSessionBlocked(String sessionId) {
		if (blockedSessions.containsKey(sessionId)) {
			long blockTime = blockedSessions.get(sessionId);
			if (System.currentTimeMillis() - blockTime > BLOCK_TIME) {
				blockedSessions.remove(sessionId);
				return false;
			}
			return true;
		}
		return false;
	}
	
	// Sumar intentos fallidos login
	private void incrementAttempts(String sessionId) {
		loginAttempts.merge(sessionId, 1, Integer::sum);
		if (loginAttempts.get(sessionId) > MAX_ATTEMPTS) {
			blockedSessions.put(sessionId, System.currentTimeMillis());
			loginAttempts.remove(sessionId);
		}
	}

	private void resetAttempts(String sessionId) {
		loginAttempts.remove(sessionId);
	}

	public boolean emailFormatoValido(User user) {
		/*boolean emailValido = false;
		if (user.comprobarFormatoEmail())
			emailValido = true;

		return emailValido;*/
		return true;
	}

}
