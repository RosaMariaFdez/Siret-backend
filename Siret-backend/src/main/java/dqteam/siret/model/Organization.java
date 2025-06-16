package dqteam.siret.model;


import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Organizations")

public class Organization {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "organization_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;
/*
    // 1: inicio/fin, 2: tiempo por proyecto, 3: horario personal
    @Column(name = "worklog_type", nullable = false)
    private int worklogType;
*/
    @Column(name = "worklog_type")
    @Enumerated(EnumType.ORDINAL)
    private EnumWorklogType worklogType;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // jefe de la organización
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boss", nullable = false)
    private User boss;

    // relación muchos a muchos a través de tabla intermedia
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserOrganization> members = new HashSet<>();

    public Organization() {
        // JPA
    }

    public Organization(String name, String description, EnumWorklogType worklogType, User boss) {
        this.name = name;
        this.description = description;
        this.worklogType = worklogType;
        this.boss = boss;
        this.createdAt = LocalDateTime.now();
    }

    //getters y setters
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public User getBoss() {
		return boss;
	}

	public void setBoss(User boss) {
		this.boss = boss;
	}

	public Set<UserOrganization> getMembers() {
		return members;
	}

	public void setMembers(Set<UserOrganization> members) {
		this.members = members;
	}
    
    
}



