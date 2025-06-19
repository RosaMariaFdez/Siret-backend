package dqteam.siret.controller;

import dqteam.siret.model.Organization;
import dqteam.siret.model.OrganizationDTO;
import dqteam.siret.service.OrganizationService;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("organizations")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<?> createOrganization(@RequestBody OrganizationDTO dto) {
        try {
            Organization created = organizationService.createOrganization(dto);

            // Puedes devolver el DTO o un mensaje
            OrganizationDTO response = new OrganizationDTO(
                    created.getId(),
                    created.getName(),
                    created.getDescription(),
                    created.getWorklogType(),
                    created.getBoss().getEmail()
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body("Acceso denegado: " + e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear la organización");
        }
    }
    
    //actualizar info organización
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateOrganization(@PathVariable UUID id, @RequestBody OrganizationDTO dto,
                                                @RequestParam String requesterEmail) {
        try {
            Organization updated = organizationService.updateOrganization(id, dto, requesterEmail);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body("Acceso denegado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar la organización");
        }
    }

    //eliminar organización
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrganization(@PathVariable UUID id, @RequestParam String requesterEmail) {
        try {
            organizationService.deleteOrganization(id, requesterEmail);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body("Acceso denegado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al eliminar la organización");
        }
    }

    //cambiar jefe
    @PutMapping("/{id}/changeBoss")
    public ResponseEntity<?> changeBoss(@PathVariable UUID id,
                                        @RequestParam String newBossEmail,
                                        @RequestParam String requesterEmail) {
        try {
            Organization updated = organizationService.changeBoss(id, newBossEmail, requesterEmail);
            
            OrganizationDTO response = new OrganizationDTO(
                    updated.getId(),
                    updated.getName(),
                    updated.getDescription(),
                    updated.getWorklogType(),
                    updated.getBoss().getEmail()
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body("Acceso denegado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al cambiar el jefe de la organización");
        }
    }


}
