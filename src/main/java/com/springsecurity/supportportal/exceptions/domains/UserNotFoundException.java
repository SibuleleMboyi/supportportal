package com.springsecurity.supportportal.exceptions.domains;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message){
        super(message);
    }
}
