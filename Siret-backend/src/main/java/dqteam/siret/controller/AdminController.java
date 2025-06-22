package dqteam.siret.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dqteam.siret.model.OrganizationSimpleDTO;
import dqteam.siret.model.UserDTO;
import dqteam.siret.service.AdminService;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')") // solo para administradores por authorities de UserDetails

public class AdminController {

    @Autowired
    private AdminService adminService;

    @PutMapping("/grantAdmin")
    public ResponseEntity<?> grantAdmin(@RequestParam String email) {
        adminService.grantAdminRole(email);
        return ResponseEntity.ok("Permiso de administrador concedido");
    }
    
    @PutMapping("/grantBoss")
    public ResponseEntity<?> grantBoss(@RequestParam String email) {
		adminService.grantBossRole(email);
		return ResponseEntity.ok("Permiso de jefe concedido");
	}

    @PutMapping("/revokeAdmin")
    public ResponseEntity<?> revokeAdmin(@RequestParam String email) {
        adminService.revokeAdminRole(email);
        return ResponseEntity.ok("Permiso de administrador revocado");
    }
    
    @PutMapping("/revokeBoss")
    public ResponseEntity<?> revokeBoss(@RequestParam String email) {
    	adminService.revokeBossRole(email);
    	return ResponseEntity.ok("Permiso de jefe revocado");
    }

    @GetMapping("/listAdmins")
    public ResponseEntity<List<UserDTO>> listAdmins() {
        return ResponseEntity.ok(adminService.getAdmins());
    }
    
    @PutMapping("/deleteUser")
    public ResponseEntity<?> deleteUser(@RequestParam String email) {
		adminService.deleteUser(email);
		return ResponseEntity.ok("Usuario eliminado");
	}
    
    //ver organizaciones de un usuario
    @GetMapping("/user-organizations/{userId}")
    public ResponseEntity<List<OrganizationSimpleDTO>> getUserOrganizations(@PathVariable UUID userId) {
        List<OrganizationSimpleDTO> organizations = adminService.getOrganizationsOfUser(userId);
        return ResponseEntity.ok(organizations);
    }
    

}
