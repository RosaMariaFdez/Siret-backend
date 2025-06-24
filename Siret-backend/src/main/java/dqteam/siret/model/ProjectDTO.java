package dqteam.siret.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProjectDTO {

	private UUID projectId;
	private String name;
	private String description;
	private LocalDateTime createdAt;
	private UUID organizationId;
	private EnumProjectStatus status;
	private LocalDate deadline;
	private UUID createdBy;

	public ProjectDTO(UUID projectId, String name, String description, LocalDateTime createdAt, UUID organizationId,
			EnumProjectStatus status, LocalDate deadline, UUID createdBy) {
		this.projectId = projectId;
		this.name = name;
		this.description = description;
		this.createdAt = createdAt;
		this.organizationId = organizationId;
		this.status = status;
		this.deadline = deadline;
		this.createdBy = createdBy;

	}

	public UUID getProjectId() {
		return projectId;
	}

	public void setProjectId(UUID projectId) {
		this.projectId = projectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public UUID getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(UUID organizationId) {
		this.organizationId = organizationId;
	}

	public EnumProjectStatus getStatus() {
		return status;
	}

	public void setStatus(EnumProjectStatus status) {
		this.status = status;
	}

	public LocalDate getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDate deadline) {
		this.deadline = deadline;
	}

	public UUID getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UUID createdBy) {
		this.createdBy = createdBy;
	}

}
