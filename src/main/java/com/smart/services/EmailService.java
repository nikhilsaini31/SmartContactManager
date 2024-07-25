package com.smart.services;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
public boolean sendEmail(String subject,String message,String to) {
		
		boolean f=false;
		
		String from="ayansaini001@gmail.com";
		
		// variable for Gmail
				String host = "smtp.gmail.com";

				// get the system properties
				Properties properties = new Properties();

				// setting important information to properties object

				// host set
				properties.put("mail.smtp.host", host);
				properties.put("mail.smtp.port", "465");
				properties.put("mail.smtp.ssl.enable", "true");
				properties.put("mail.smtp.auth", "true");
		        properties.put("mail.smtp.starttls.enable", "true"); // Added TLS support

				final String user = "ayansaini001@gmail.com"; // Corrected the username
				final String password = "zjqj nlfc avsk nyfp"; // Replace with your actual password

				// step:1 to get the session object...
				Session session = Session.getInstance(properties, new Authenticator() {

					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(user, password);
					}
				});

				session.setDebug(true);

				// step:2 compose the message[text,multimedia]

				MimeMessage mm = new MimeMessage(session);

				try {

					// sent from (email) who's sending
					mm.setFrom(new InternetAddress(from));

					// adding recipient (receiver) to message
					mm.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

					// adding subject to message
					mm.setSubject(subject);

					// adding text to message
					mm.setText(message);

					// step:3 send the message using transport class

					Transport.send(mm);

					System.out.println("sending successfully....");

					f=true;
					
				} catch (Exception e) {
					e.printStackTrace();
				}
		
				
		return f;
	}
	

}
