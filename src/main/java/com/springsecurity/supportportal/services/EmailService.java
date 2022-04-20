package com.springsecurity.supportportal.services;

import static com.springsecurity.supportportal.constants.EmailConstants.*;

import java.util.Properties;

import javax.mail.Session;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private Session getEmailSession(){
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_START_TLS_ENABLE, true);
        properties.put(SMTP_START_TLS_REQUIRED, true);

        return Session.getInstance(properties, null);

    }
}
