package com.springsecurity.supportportal.listeners;

import java.security.Principal;
import java.util.concurrent.ExecutionException;

import com.springsecurity.supportportal.services.LoginAttemptsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;

public class AuthenticationFailureListener {
    @Autowired
    private LoginAttemptsService loginAttemptService;

    // Every time a User fails to login, AuthenticationFailureBadCredentialsEvent gets fired.
    // We listen to this event and register the User in the LoginAttemptCache
    @EventListener
    public void AuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) throws ExecutionException{
        Object principle = event.getAuthentication().getPrincipal();
        if(principle instanceof String){
            String username = (String) event.getAuthentication().getPrincipal();
            loginAttemptService.addUserToLoginAttemptCache(username);
        }
    }
}
