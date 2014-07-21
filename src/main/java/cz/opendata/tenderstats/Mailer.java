package cz.opendata.tenderstats;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Sends mails using configured smtp server.
 * 
 * @author Matej Snoha
 */
public class Mailer {

	private static String host = "localhost";
	private static String port = "25";

	private String from;
	private String to;
	private String subject;
	private String text;

	/**
	 * Constructs new Mailer with prepared message.
	 * 
	 * @param from
	 *            Sender e-mail address.
	 * @param to
	 *            Recipient e-mail address.
	 * @param subject
	 *            Message subject.
	 * @param text
	 *            Message text.
	 */
	public Mailer(String from, String to, String subject, String text) {
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.text = text;
	}

	/**
	 * Sends prepared message.
	 * 
	 * @return True if mail has been successfully sent.
	 */
	public boolean send() {

		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		Session mailSession = Session.getDefaultInstance(props);
		Message message = new MimeMessage(mailSession);

		try {
			InternetAddress fromAddress = new InternetAddress(from);
			InternetAddress toAddress = new InternetAddress(to);

			message.setFrom(fromAddress);
			message.setRecipient(RecipientType.TO, toAddress);
			message.setSubject(subject);
			message.setText(text);

			Transport.send(message);
		} catch (MessagingException unused) {
			return false;
		}
		return true;
	}
}
