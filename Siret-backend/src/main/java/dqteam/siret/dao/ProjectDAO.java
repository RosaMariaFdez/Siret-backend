package dqteam.siret.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


import org.springframework.data.jpa.repository.JpaRepository;
import dqteam.siret.model.Project;

public interface ProjectDAO extends JpaRepository<Project, UUID> {

	// sacar proyectos de una organización
    List<Project> findByOrganization_OrganizationId(UUID organizationId);

    // sacar proyectos creados de un usuario
    List<Project> findByCreatedBy_UserId(UUID userId);

    // sacar proyecto por su ID y organización
    Optional<Project> findByProjectIdAndOrganization_OrganizationId(UUID projectId, UUID organizationId);
    // sacar proyecto por su nombre y organización
    boolean existsByNameAndOrganization_OrganizationId(String name, UUID organizationId);
}

