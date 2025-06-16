package dqteam.siret.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import dqteam.siret.model.OrganizationDTO;
import dqteam.siret.model.User;
import dqteam.siret.security.TokenService;
import dqteam.siret.security.UserPrincipal;
import dqteam.siret.service.EmailService;
import dqteam.siret.service.UserService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("users")
public class UserController {
	private final UserService userService;
	private final TokenService tokenService; // este servicio para manejar JWT
	private final EmailService emailService;
	
	public UserController(UserService userService, TokenService tokenService, EmailService emailService) {
		this.userService = userService;
		this.tokenService = tokenService;
		this.emailService = emailService;
	}
	
	@PostMapping("/registro")
	public ResponseEntity<String> registrar(@RequestBody User user) {
		try {
			userService.registerUser(user);
			return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado correctamente");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody User user, HttpSession session) {
        try {
               User loggedInUser = userService.login(user, session);
               UserPrincipal userPrincipal = new UserPrincipal(loggedInUser);
               Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
               String jwtToken = tokenService.generateToken(authentication);

               // Crear cookie httpOnly
               ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
               .httpOnly(true)
               .secure(false) // Usa true en producción con HTTPS
               .path("/")
               .maxAge(3600)
               .sameSite("Lax") // O Strict para más seguridad supuestamente
               .build();

               return ResponseEntity.ok()
                       .header("Set-Cookie", cookie.toString())
                       .body("Usuario logueado correctamente");
            } catch (ResponseStatusException e) {
                return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        	
          
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    	
	}	
	
	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpSession session) {
		session.invalidate(); // Invalida la sesión actual
		ResponseCookie cookie = ResponseCookie.from("jwt", "").httpOnly(true).secure(false) // Usa true en producción
																							// con HTTPS
				.path("/").maxAge(0) // Elimina la cookie
				.sameSite("Lax").build();

		return ResponseEntity.ok().header("Set-Cookie", cookie.toString()).body("Usuario deslogueado correctamente");
	}

	@GetMapping("/{email}/organizations")
	public ResponseEntity<List<OrganizationDTO>> getOrganizationsForUser(@PathVariable String email) {
	    List<OrganizationDTO> organizations = userService.getOrganizationsForUser(email);
	    return ResponseEntity.ok(organizations);
	}
	@PostMapping("/forgotPassword")
	public ResponseEntity<String> forgotPassword(@RequestParam String email) {
	    try {
	        userService.enviarTokenRecuperacion(email);
	        return ResponseEntity.ok("Se ha enviado un correo con las instrucciones para recuperar la contraseña");
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    }
	}

	@PostMapping("/resetPassword")
	public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String nuevaPassword) {
	    try {
	        userService.cambiarPasswordConToken(token, nuevaPassword);
	        return ResponseEntity.ok("Contraseña cambiada correctamente");
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}

}
