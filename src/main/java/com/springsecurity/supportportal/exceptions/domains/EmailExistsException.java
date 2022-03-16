package com.springsecurity.supportportal.exceptions.domains;

public class EmailExistsException extends Exception{
    public EmailExistsException(String message){
        super(message);
    }
}
