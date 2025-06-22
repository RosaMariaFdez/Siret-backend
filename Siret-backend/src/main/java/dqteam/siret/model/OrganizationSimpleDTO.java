package dqteam.siret.model;

import java.util.UUID;

public class OrganizationSimpleDTO {
    private UUID id;
    private String name;
    private String description;

    public OrganizationSimpleDTO(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}
