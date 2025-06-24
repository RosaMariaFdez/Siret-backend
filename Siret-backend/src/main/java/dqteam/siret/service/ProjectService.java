package dqteam.siret.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;

import org.springframework.stereotype.Service;

import dqteam.siret.dao.OrganizationDAO;
import dqteam.siret.dao.ProjectDAO;
import dqteam.siret.dao.UserDAO;
import dqteam.siret.model.Organization;
import dqteam.siret.model.Project;
import dqteam.siret.model.ProjectDTO;
import dqteam.siret.model.User;

@Service
public class ProjectService {
	private final ProjectDAO projectDAO;
	private final OrganizationDAO organizationDAO;
	private final UserDAO userDAO;
	
	public ProjectService(ProjectDAO projectDAO, OrganizationDAO organizationDAO, UserDAO userDAO) {
		this.projectDAO = projectDAO;
		this.organizationDAO = organizationDAO;
		this.userDAO = userDAO;
	}
	
	public Project createProject(ProjectDTO dto) {
	    // Obtener el usuario autenticado
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String loggedEmail = auth.getName(); // email del usuario logueado

	    // Buscar al creador por ID
	    Optional<User> optionalCreator = userDAO.findById(dto.getCreatedBy());
	    if (optionalCreator.isEmpty()) {
	        throw new IllegalArgumentException("No se encontró un usuario con ese ID");
	    }

	    User creator = optionalCreator.get();

	    // Verificar que sea él mismo el autenticado
	    if (!creator.getEmail().equalsIgnoreCase(loggedEmail)) {
	        throw new AccessDeniedException("No puedes crear un proyecto a nombre de otro usuario");
	    }

	    // Buscar organización
	    Organization organization = organizationDAO.findById(dto.getOrganizationId())
	        .orElseThrow(() -> new IllegalArgumentException("Organización no encontrada"));

	    // Verificar si es admin global
	    boolean isAdmin = creator.isAdmin();

	    // Verificar si es el jefe de esta organización
	    boolean isBossOfOrg = organization.getBoss().getId().equals(creator.getId());

	    if (!isAdmin && !isBossOfOrg) {
	        throw new AccessDeniedException("Solo los administradores o el jefe de la organización pueden crear proyectos");
	    }

	    // Crear y guardar el proyecto
	    Project project = new Project(
	        dto.getName(),
	        dto.getDescription(),
	        LocalDateTime.now(),
	        organization,
	        dto.getStatus(),
	        dto.getDeadline(),
	        creator
	    );

	    return projectDAO.save(project);
	}

	//eliminar proyecto
	public void deleteProject(UUID projectId, UUID organizationId) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String loggedEmail = auth.getName();

	    if (loggedEmail == null || loggedEmail.isEmpty()) {
	        throw new AccessDeniedException("Usuario no autenticado");
	    }

	    // Buscar el proyecto por ID y organización
	    Optional<Project> optionalProject = projectDAO.findByProjectIdAndOrganization_OrganizationId(projectId, organizationId);
	    if (optionalProject.isEmpty()) {
	        throw new IllegalArgumentException("Proyecto no encontrado en la organización especificada");
	    }

	    Project project = optionalProject.get();

	    User loggedUser = project.getCreatedBy(); 
	    

	    // Verificación: es admin o jefe de la organización
	    if (!project.getCreatedBy().getEmail().equalsIgnoreCase(loggedEmail)
	        && !project.getCreatedBy().isAdmin()
	        && !project.getOrganization().getBoss().getEmail().equalsIgnoreCase(loggedEmail)) {
	        throw new AccessDeniedException("No tienes permiso para eliminar este proyecto");
	    }

	    projectDAO.delete(project);
	}


}
