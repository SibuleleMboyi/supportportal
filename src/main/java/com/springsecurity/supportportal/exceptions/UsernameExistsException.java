package com.springsecurity.supportportal.exceptions;

public class UsernameExistsException extends Exception{
    public UsernameExistsException(String message){
        super(message);
    }
}
