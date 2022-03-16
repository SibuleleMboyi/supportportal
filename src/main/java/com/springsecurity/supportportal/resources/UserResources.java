package com.springsecurity.supportportal.resources;

import com.springsecurity.supportportal.domains.User;
import com.springsecurity.supportportal.services.UserService;
import com.springsecurity.supportportal.exceptions.UsernameExistsException;
import com.springsecurity.supportportal.exceptions.domains.EmailExistsException;
import com.springsecurity.supportportal.exceptions.domains.ExceptionHandling;
import com.springsecurity.supportportal.exceptions.domains.UserNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

// URLs for this class start by "/user"
@RequestMapping(path = { "/", "/user" })
public class UserResources extends ExceptionHandling {

    // We don't directly call the "UserServiceImple.java "class.
    // We call the service instead. ( I don't know why!)
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, UsernameExistsException, EmailExistsException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(),
                user.getEmail());
        return new ResponseEntity<User>(newUser, HttpStatus.OK);

    }
}
