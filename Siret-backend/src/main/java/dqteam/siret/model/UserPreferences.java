package dqteam.siret.model;

import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Table(name = "UserPreferences")
public class UserPreferences {

	@Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "receive_reminders", nullable = false)
    private boolean receiveReminders;

    @Column(name = "dark_mode", nullable = false)
    private boolean darkMode;
	
	// Constructor
	public UserPreferences() {
		this.receiveReminders = true;
		this.darkMode = false;
	}

	
	// Getters y Setters
	public UUID getUserId() {
		return userId;
	}

	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public void setUserId(UUID userId) {
		this.userId = userId;
	}
	
	public boolean isReceiveReminders() {
		return receiveReminders;
	}

	public void setReceiveReminders(boolean receiveReminders) {
		this.receiveReminders = receiveReminders;
	}
	
	public boolean isDarkMode() {
		return darkMode;
	}

	public void setDarkMode(boolean darkMode) {
		this.darkMode = darkMode;
	}
	
}
