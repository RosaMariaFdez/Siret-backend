package dqteam.siret.model;


import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable

public class UserOrganizationId implements Serializable {

    private UUID userId;
    private UUID organizationId;

    public UserOrganizationId() {
        // JPA
    }

    public UserOrganizationId(UUID userId, UUID organizationId) {
        this.userId = userId;
        this.organizationId = organizationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserOrganizationId that)) return false;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(organizationId, that.organizationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, organizationId);
    }
    
    //getters y setters
	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public UUID getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(UUID organizationId) {
		this.organizationId = organizationId;
	}
    
}
