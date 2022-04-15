package com.springsecurity.supportportal.listeners;

import java.util.concurrent.ExecutionException;

import com.springsecurity.supportportal.domains.User;
import com.springsecurity.supportportal.services.LoginAttemptsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener {
    @Autowired
    private LoginAttemptsService loginAttemptService;

        // Every time a User fails to login, AuthenticationSuccessEvent gets fired.
    // We listen to this event and register the User in the LoginAttemptCache

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) throws ExecutionException{
        Object principle = event.getAuthentication().getPrincipal();
        if(principle instanceof User){
            User user = (User) event.getAuthentication().getPrincipal();
            loginAttemptService.evictUserFromLoginCache(user.getUsername());
        }
    }
}
