package dqteam.siret.model;

import java.util.UUID;

public class UserDTO {
    private UUID id;
    private String name;
    private String surname;
    private String email;
    private boolean isBoss;
    private boolean isAdmin;

    // Constructores
    public UserDTO() {}

    public UserDTO(UUID id, String name, String surname, String email, boolean isBoss, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.isBoss = isBoss;
        this.isAdmin = isAdmin;
    }

    // Getters y Setters
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isBoss() {
        return isBoss;
    }

    public void setBoss(boolean isBoss) {
        this.isBoss = isBoss;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
