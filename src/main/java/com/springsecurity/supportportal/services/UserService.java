package com.springsecurity.supportportal.services;

import java.util.List;

import com.springsecurity.supportportal.domains.User;
import com.springsecurity.supportportal.exceptions.UsernameExistsException;
import com.springsecurity.supportportal.exceptions.domains.EmailExistsException;
import com.springsecurity.supportportal.exceptions.domains.UserNotFoundException;

import org.springframework.stereotype.Service;

@Service
public interface UserService {
     User register(String firstName, String lastName, String username,String email) throws UserNotFoundException, UsernameExistsException, EmailExistsException;
     List<User> getUsers();
     User findUserByUsername(String username);
     User findUserByEmail(String email);
}
