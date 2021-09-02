package com.bit.plugins;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailSender {
	Properties prop = new Properties();
	Session session;
	Message message;
	MimeBodyPart mimeBodyPart;
	Multipart multipart;
	private boolean auth;
	private boolean enableStartTLS;
	private String host;
	private int port;
	private String trustSSL;
	private String username;
	private String password;
	private String from;
	private String to;
	private String subject;
	private String content;
	
	public EmailSender() {
		
	}
	public EmailSender isAuth(boolean auth) {
		this.auth = auth;
		return this;
	}
	
	public EmailSender isEnableStartTLS(boolean condition) {
		this.enableStartTLS = condition;
		return this;
	}
	
	public EmailSender withHost(String host) {
		this.host = host;
		return this;
	}
	
	public EmailSender withPort(int port) {
		this.port = port;
		return this;
	}
	
	public EmailSender withTrustSSL(String trustSSL) {
		this.trustSSL = trustSSL;
		return this;
	}
	
	public EmailSender withUsername(String username) {
		this.username = username;
		return this;
	}
	
	public EmailSender withPassword(String password) {
		this.password = password;
		return this;
	}
	
	public EmailSender from(String from) {
		this.from = from;
		return this;
	}
	
	public EmailSender to(String to) {
		this.to = to;
		return this;
	}
	
	public EmailSender withSubject(String subject) {
		this.subject = subject;
		return this;
	}
	
	public EmailSender withContent(String content) {
		this.content = content;
		return this;
	}
	
	public void send() {
		try {
			sendMessageByProperties();
		} catch (AddressException e) {
			setExceptionMessage(e);
		} catch (MessagingException e) {
			setExceptionMessage(e);
		}
	}
	
	private void sendMessageByProperties() throws AddressException, MessagingException {
		setProperties();
		setSessionWithProperties(prop);
		setMessageWithFromToAndSubject();
		setMimeBodyPart();
		setMultipartBodyPart();
		message.setContent(multipart);
		Transport.send(message);
	}
	
	private void setExceptionMessage(Exception e) {
		e.printStackTrace();			
	}
	
	private void setMultipartBodyPart() throws MessagingException {
		multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);
	}
	
	private void setMimeBodyPart() throws MessagingException {
		mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(this.content, "text/html");
	}
	
	private void setMessageWithFromToAndSubject() throws AddressException, MessagingException {
		message = new MimeMessage(session);
		message.setFrom(new InternetAddress("no-reply@bhakti.co.id"));
		message.setRecipients(
				  Message.RecipientType.TO, InternetAddress.parse(this.to));
		message.setSubject(this.subject);
	}
	
	private void setProperties() {
		prop.put("mail.smtp.auth", auth);
		prop.put("mail.smtp.starttls.enable", enableStartTLS ? "true" :  "false");
		prop.put("mail.smtp.host", host);
		prop.put("mail.smtp.port", port + "");
		prop.put("mail.smtp.ssl.trust", trustSSL);
	}
	
	private void setSessionWithProperties(Properties prop) {
		session =  Session.getInstance(prop, new Authenticator() {
		    @Override
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication(username, password);
		    }
		});
	}
}
