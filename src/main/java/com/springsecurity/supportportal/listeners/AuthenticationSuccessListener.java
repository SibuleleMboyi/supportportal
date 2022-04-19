package com.springsecurity.supportportal.listeners;

import com.springsecurity.supportportal.domains.UserPrincipal;
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
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event){
        Object principle = event.getAuthentication().getPrincipal();
        if(principle instanceof UserPrincipal){
            UserPrincipal userPrincipal = (UserPrincipal) event.getAuthentication().getPrincipal();
            loginAttemptService.evictUserFromLoginCache(userPrincipal.getUsername());
        }
    }
}
