package dqteam.siret.controller;

import dqteam.siret.model.Project;
import dqteam.siret.model.ProjectDTO;
import dqteam.siret.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // Crear proyecto
    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody ProjectDTO dto) {
        try {
            Project created = projectService.createProject(dto);

            ProjectDTO response = new ProjectDTO(
                    created.getProjectId(),
                    created.getName(),
                    created.getDescription(),
                    created.getCreatedAt(),
                    created.getOrganization().getId(),
                    created.getStatus(),
                    created.getDeadline(),
                    created.getCreatedBy().getId()
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body("Acceso denegado: " + e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear el proyecto");
        }
    }

    // Eliminar proyecto
    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable UUID projectId, UUID organizationId) {
        try {
            projectService.deleteProject(projectId, organizationId);
            return ResponseEntity.ok("Proyecto eliminado correctamente");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body("Acceso denegado: " + e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al eliminar el proyecto");
        }
    }
}
