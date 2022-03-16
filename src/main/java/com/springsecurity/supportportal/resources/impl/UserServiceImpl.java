package com.springsecurity.supportportal.resources.impl;

import static com.springsecurity.supportportal.constants.SecurityConstants.*;
import com.springsecurity.supportportal.domains.User;
import com.springsecurity.supportportal.domains.UserPrincipal;
import static com.springsecurity.supportportal.enumerations.Role.*;
import com.springsecurity.supportportal.exceptions.UsernameExistsException;
import com.springsecurity.supportportal.exceptions.domains.EmailExistsException;
import com.springsecurity.supportportal.exceptions.domains.UserNotFoundException;
import com.springsecurity.supportportal.repositories.UserRepository;
import com.springsecurity.supportportal.services.UserService;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.List;

// This class is run when the User tries to login into the system.
// So it checks if the user is already in the database yet.
// Spring Security needs "UserDetailsService" class to leverage the security aspect of getting the User
// and to check if the User provided the correct credentials.

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    // Logs or shows all the events that are happening.
    // "getClass()" can be replaced by the name of this class, i.e
    // "UserService.class".
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bcryptPasswordEncoder;

    // Is called whenever Spring Security is trying to check the authentication of
    // the User credentials.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            LOGGER.error(NO_USER_FOUND_BY_SURNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_SURNAME + username);
        } else {
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info("Returning found User by Username: " + username);
            return userPrincipal;
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email)
            throws UserNotFoundException, UsernameExistsException, EmailExistsException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodedPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinedDate(new Date());
        user.setPassword(encodedPassword);
        user.setIsActive(true);
        user.setIsNotLocked(true);
        user.setRole(ROLE_USER.name()); // gets string from an enum
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfieImageUrl(getTemporalyProfileUrl());
        userRepository.save(user);
        LOGGER.info("New user password " + password);
        return user;
    }

    private String getTemporalyProfileUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH).toUriString();
    }

    private String encodedPassword(String password) {
        return bcryptPasswordEncoder.encode(password);
    }

    // Returns a string made up of a combination of numbers and letters
    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {

        return RandomStringUtils.randomNumeric(10);
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail)
            throws UserNotFoundException, UsernameExistsException, EmailExistsException {

        User userByNewUsername = findUserByUsername(newUsername);
        User currentUser = findUserByUsername(currentUsername);
        User userByEmail = findUserByEmail(newEmail);

        // This part of the program is executed when a User is updating their
        // information, i.e Username or Email
        if (!StringUtils.isBlank(currentUsername)) {
            if (currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_SURNAME + currentUsername);
            }

            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistsException(USERNAME_ALREADY_EXISTS);
            }

            if (userByEmail != null && !currentUser.getId().equals(userByEmail.getId())) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }

            return currentUser;
        }
        // This part of the program is executed when validating a User, when they
        // register for the 1st time.
        else {
            if (userByNewUsername != null) {
                throw new UsernameExistsException(USERNAME_ALREADY_EXISTS);
            }

            if (userByEmail != null) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }

            return null;
        }
    }

    @Override
    public List<User> getUsers() {

        return userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {

        return userRepository.findUserByEmail(email);
    }

}
