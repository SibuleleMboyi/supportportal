package com.springsecurity.supportportal.services;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.springsecurity.supportportal.domains.User;
import com.springsecurity.supportportal.exceptions.UsernameExistsException;
import com.springsecurity.supportportal.exceptions.domains.EmailExistsException;
import com.springsecurity.supportportal.exceptions.domains.UserNotFoundException;

import org.springframework.stereotype.Service;

@Service
public interface UserService {
     User register(String firstName, String lastName, String username,String email) throws UserNotFoundException, UsernameExistsException, EmailExistsException, AddressException, MessagingException;
     List<User> getUsers();
     User findUserByUsername(String username);
     User findUserByEmail(String email);
}
