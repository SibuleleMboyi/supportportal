package com.springsecurity.supportportal.services;

import static com.springsecurity.supportportal.constants.EmailConstants.*;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import com.sun.mail.smtp.SMTPTransport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendNewPasswordEmail(String firstName, String password, String email)
            throws AddressException, MessagingException {
        Message message = createEmail(firstName, password, email);
        SMTPTransport smtpTrasnport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTrasnport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTrasnport.sendMessage(message, message.getAllRecipients());
        smtpTrasnport.close();
    }

    private Message createEmail(String firstName, String password, String email)
            throws AddressException, MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject(EMAIL_SUBJECT);

        message.setText(
                "Hello " + firstName + " \n \nYour new account passsword is: " + password + "\n \n The Support Team");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;

    }

    private Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_START_TLS_ENABLE, true);
        properties.put(SMTP_START_TLS_REQUIRED, true);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        return Session.getInstance(properties, null);

    }
}
