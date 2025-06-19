package dqteam.siret.controller;

import dqteam.siret.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/invitations")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    @PostMapping("/send")
    public ResponseEntity<?> sendInvitation(@RequestParam UUID organizationId,
                                            @RequestParam String email,
                                            @RequestParam String requesterEmail) {
        try {
            invitationService.sendInvitation(organizationId, email, requesterEmail);
            return ResponseEntity.ok("Invitación enviada correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al enviar la invitación");
        }
    }
    
    @PostMapping("/accept")
    public ResponseEntity<?> acceptInvitation(@RequestParam String token, @RequestParam String userEmail) {
        try {
            invitationService.acceptInvitation(token, userEmail);
            return ResponseEntity.ok("Invitación aceptada correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/reject")
    public ResponseEntity<?> rejectInvitation(@RequestParam String token, @RequestParam String userEmail) {
        try {
            invitationService.rejectInvitation(token, userEmail);
            return ResponseEntity.ok("Invitación rechazada correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingInvitations(@RequestParam String userEmail) {
        try {
            return ResponseEntity.ok(invitationService.getPendingInvitations(userEmail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
