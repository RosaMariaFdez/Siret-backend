package dqteam.siret.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "UserOrganizations")

public class UserOrganization {

    @EmbeddedId
    private UserOrganizationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("organizationId")
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    public UserOrganization() {
        // JPA
    }

    public UserOrganization(User user, Organization organization) {
        this.user = user;
        this.organization = organization;
        this.id = new UserOrganizationId(user.getId(), organization.getId());
        this.joinedAt = LocalDateTime.now();
    }

	// Getters and Setters
	public UserOrganizationId getUserId() {
		return id;
	}

	public void setUserId(UserOrganizationId userId) {
		this.id = userId;
	}
	public UserOrganizationId getId() {
		return id;
	}

	public void setId(UserOrganizationId id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public LocalDateTime getJoinedAt() {
		return joinedAt;
	}

	public void setJoinedAt(LocalDateTime joinedAt) {
		this.joinedAt = joinedAt;
	}
    
    
}

