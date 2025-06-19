package dqteam.siret.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class InvitationDTO {

    private UUID id;
    private UUID organizationId;
    private String organizationName;
    private String email;
    private String token;
    private LocalDateTime sentAt;
    private LocalDateTime expiresAt;

    public InvitationDTO() {}

    public InvitationDTO(UUID id, UUID organizationId, String organizationName, String email, String token,
                         LocalDateTime sentAt, LocalDateTime expiresAt) {
        this.id = id;
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.email = email;
        this.token = token;
        this.sentAt = sentAt;
        this.expiresAt = expiresAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrganizationId() {
        return organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public LocalDateTime getSentAt() {
		return sentAt;
	}

	public void setSentAt(LocalDateTime sentAt) {
		this.sentAt = sentAt;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public void setOrganizationId(UUID organizationId) {
		this.organizationId = organizationId;
	}
}
