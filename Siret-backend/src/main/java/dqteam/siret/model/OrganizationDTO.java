package dqteam.siret.model;

import java.util.UUID;

import lombok.Data;


public class OrganizationDTO {
    private UUID id; // puede ser null en la creación
    private String name;
    private String description;
    private EnumWorklogType worklogType;
    private String bossEmail; // identificador del creador/jefe

    public OrganizationDTO() {
        
    }

    public OrganizationDTO(UUID id, String name, String description, EnumWorklogType worklogType, String bossEmail) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.worklogType = worklogType;
        this.bossEmail = bossEmail;
    }

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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

	public EnumWorklogType getWorklogType() {
		return worklogType;
	}

	public void setWorklogType(EnumWorklogType worklogType) {
		this.worklogType = worklogType;
	}

	public String getBossEmail() {
		return bossEmail;
	}

	public void setBossEmail(String bossEmail) {
		this.bossEmail = bossEmail;
	}
    
    
}