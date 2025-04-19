package dqteam.siret.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import dqteam.siret.model.User;
import dqteam.siret.service.UserService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("users")
public class UserController {
	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
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
	/*
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody User user, HttpSession session) {
        try {
               User loggedInUser = userService.login(user);
                session.setAttribute("userId", loggedInUser.getUserId());
                return ResponseEntity.ok("Usuario logueado correctamente");
            } catch (ResponseStatusException e) {
                return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        	
          
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }	
	}*/	

}
