package com.springsecurity.supportportal.resources;

import static com.springsecurity.supportportal.constants.SecurityConstants.JWT_TOKEN_HEADER;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.springsecurity.supportportal.domains.User;
import com.springsecurity.supportportal.domains.UserPrincipal;
import com.springsecurity.supportportal.utilities.JWTTokenProvider;
import com.springsecurity.supportportal.exceptions.UsernameExistsException;
import com.springsecurity.supportportal.exceptions.domains.EmailExistsException;
import com.springsecurity.supportportal.exceptions.domains.ExceptionHandling;
import com.springsecurity.supportportal.exceptions.domains.UserNotFoundException;
import com.springsecurity.supportportal.resources.impl.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Figure out why the Email service is not working
 * **/
@RestController

// URLs for this class start by "/user"
@RequestMapping(path = { "/", "/user" })
public class UserResources extends ExceptionHandling {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());
        User loggedInUser = userServiceImpl.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loggedInUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<User>(loggedInUser, jwtHeader, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user)
            throws UserNotFoundException, UsernameExistsException, EmailExistsException, AddressException, MessagingException {
        User newUser = userServiceImpl.register(user.getFirstName(), user.getLastName(), user.getUsername(),
                user.getEmail());
        return new ResponseEntity<User>(newUser, HttpStatus.OK);

    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

}
