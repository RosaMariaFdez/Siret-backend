package dqteam.siret;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import dqteam.siret.model.User;
import dqteam.siret.service.UserService;

@SpringBootApplication
public class SiretApplication implements CommandLineRunner {

	@Autowired
	UserService userService;
	
	public static void main(String[] args) {
		SpringApplication.run(SiretApplication.class, args);
	}
	
	@Override
    public void run(String... args) throws Exception {
		
        // Aquí probar el registro de un usuario o cualquier otra funcionalidad directamente
        System.out.println("Probando el registro de usuario...");
        
        // Crear un nuevo usuario de ejemplo
        User user = new User("Juani", "Caravantes","juani@example.com", "password123");
        
        // Llamada al servicio para registrar al usuario
        userService.registerUser(user);
        
        // Imprimir mensaje de éxito
        System.out.println("Usuario registrado: " + user.getName());
        
        
    }
}
