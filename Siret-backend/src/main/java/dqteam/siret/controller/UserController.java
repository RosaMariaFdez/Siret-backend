package dqteam.siret.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import dqteam.siret.model.User;
import dqteam.siret.security.TokenService;
import dqteam.siret.security.UserPrincipal;
import dqteam.siret.service.UserService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("users")
public class UserController {
	private final UserService userService;
	private final TokenService tokenService; // Asegúrate de tener este servicio para manejar JWT
	
	public UserController(UserService userService, TokenService tokenService) {
		this.userService = userService;
		this.tokenService = tokenService;
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
               .sameSite("Lax") // O Strict si prefieres más seguridad
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
		

}
