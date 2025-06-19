package dqteam.siret.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import dqteam.siret.model.User;

@Service
public class EmailService {
	private final JavaMailSender mailSender;

	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}


	public void sendPasswordResetEmail(User user, String token) {
		String resetUrl = "http://localhost:8080,/password/reset?token=" + token;

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("Restablecimiento de contraseña");
		message.setText("\t\t----RESTABLECER CONTRASEÑA----\n \nTienes 30 minutos para reestablecer tu contraseña"
				+ "Haz clic en el siguiente enlace para restablecerla: " + resetUrl +"\n\nSi no solicitaste este cambio, ignora este mensaje");

		mailSender.send(message);
	}
	
	public void sendInvitationEmailNuevoUsuario(String email, String token) {
		String registrationUrl = "http://localhost:8080/users/registro" + token;

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("\t\tInvitación a SIRET - Sistema de Resgistro de Trabajo");
		message.setText("Has sido invitado a unirte a una organización. Debes registrarte en el siguiente enlace para poder aceptar la invitación.\n\n"
				+ registrationUrl + "\n Una vez dentro consulta tu panel de notificaciones en la parte superior derecha, gracias"
						+ "\n\nSi no solicitaste esta invitación, ignora este mensaje.");

		mailSender.send(message);
	}
	
	public void sendInvitationEmailUsuario(String email, String token, String organizationName) {
		String invitationUrl = "http://localhost:8080/invitations/accept?token=" + token;

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("\t\tInvitación a SIRET - Sistema de Resgistro de Trabajo");
		message.setText("Has sido invitado a unirte a la organización '"+ organizationName +"'. Haz clic en el siguiente enlace para aceptar la invitación:\n\n"
				+ invitationUrl + "\n\nSi no solicitaste esta invitación, ignora este mensaje.");

		mailSender.send(message);
	}
	
	
}
