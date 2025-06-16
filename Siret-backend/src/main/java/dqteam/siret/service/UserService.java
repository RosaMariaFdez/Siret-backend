package dqteam.siret.service;

import java.time.Duration;
import java.time.Instant;

import java.util.List;
import java.util.Optional;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dqteam.siret.dao.PasswordResetTokenDAO;
import dqteam.siret.dao.UserDAO;
import dqteam.siret.dao.UserOrganizationDAO;
import dqteam.siret.model.Organization;
import dqteam.siret.model.OrganizationDTO;
import dqteam.siret.model.PasswordResetToken;
import dqteam.siret.model.User;
import dqteam.siret.model.UserOrganization;
import dqteam.siret.model.UserPreferences;
import dqteam.siret.security.TokenService;
import jakarta.servlet.http.HttpSession;

@Service
public class UserService {
	private static final int MAX_ATTEMPTS = 3;
	private static final long BLOCK_TIME = (15 * 60 * 1000L); // 15 minutos de bloqueo

	private final UserDAO userDAO;
	private final PasswordResetTokenDAO tokenDAO;
	private final BCryptPasswordEncoder passwordEncoder;
	private final UserOrganizationDAO userOrganizationDAO;
	private final TokenService tokenService;
	private final EmailService emailService;


	private ConcurrentHashMap<String, Integer> loginAttempts = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, Long> blockedSessions = new ConcurrentHashMap<>();

	public UserService(UserDAO userDAO, BCryptPasswordEncoder passwordEncoder, UserOrganizationDAO userOrganizationDAO, 
			PasswordResetTokenDAO tokenDAO, TokenService tokenService, EmailService emailService) {
		this.userDAO = userDAO;
		this.passwordEncoder = passwordEncoder;
		this.userOrganizationDAO = userOrganizationDAO;
		this.tokenService = tokenService;
		this.tokenDAO = tokenDAO;
		this.emailService = emailService;
		
	}

	public User registerUser(User user) {
		if (userDAO.existsByEmail(user.getEmail())) {
			throw new IllegalArgumentException("Este email ya está en uso");
		}
		/*	ESTO AL FRONT
		if (user.comprobarFormatoPassword()) {
			throw new IllegalArgumentException("Formato de contraseña incorrecto");
		}
		*/
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

	// sacar organizaciones de un usuario
	public List<OrganizationDTO> getOrganizationsForUser(String userEmail) {
	    Optional<User> optionalUser = userDAO.findByEmail(userEmail);
	    if (optionalUser.isEmpty()) {
	        throw new IllegalArgumentException("Usuario no encontrado con email: " + userEmail);
	    }

	    User user = optionalUser.get();
	    List<UserOrganization> memberships = userOrganizationDAO.findByUserOrgs(user.getId());

	    return memberships.stream()
	            .map(assoc -> {
	                Organization org = assoc.getOrganization();
	                return new OrganizationDTO(
	                        org.getId(),
	                        org.getName(),
	                        org.getDescription(),
	                        org.getWorklogType(),
	                        org.getBoss().getEmail()
	                );
	            })
	            .collect(Collectors.toList());
	}
	
	private User findUserByEmail(String email) {
		Optional<User> userOpt = userDAO.findByEmail(email);

		if (!userOpt.isPresent()) {
			throw new UsernameNotFoundException("Usuario no encontrado");
		}

		return userOpt.get();
	}
	
	///Recupercación de contraseña
	public void cambiarPasswordConToken(String token, String nuevaPassword) {
        PasswordResetToken resetToken = tokenDAO.findById(token)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token no encontrado"));

        if (resetToken.isUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este token ya ha sido usado");
        }

        if (resetToken.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expirado");
        }

        String email = tokenService.validatePasswordResetToken(token);
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(nuevaPassword));
        userDAO.save(user);

        resetToken.setUsed(true);
        tokenDAO.save(resetToken);
    }

    public void enviarTokenRecuperacion(String email) {
        User user = findUserByEmail(email);
        
        Instant expiry = Instant.now().plus(Duration.ofMinutes(30)); //30 minutos hasta que caduque el token
        String token = tokenService.generatePasswordResetToken(user.getEmail());
        
        PasswordResetToken tokenEntity = new PasswordResetToken(token, user, expiry);

        tokenDAO.save(tokenEntity);

        emailService.sendPasswordResetEmail(user, token);
    }

}
