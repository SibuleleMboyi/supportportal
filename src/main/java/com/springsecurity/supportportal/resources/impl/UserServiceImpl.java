package com.springsecurity.supportportal.resources.impl;

import static com.springsecurity.supportportal.constants.SecurityConstants.*;
import static com.springsecurity.supportportal.constants.FileConstants.*;

import static com.springsecurity.supportportal.constants.FileConstants.DEFAULT_USER_IMAGE_PATH;
import com.springsecurity.supportportal.domains.User;
import com.springsecurity.supportportal.domains.UserPrincipal;
import com.springsecurity.supportportal.enumerations.Role;

import static com.springsecurity.supportportal.enumerations.Role.*;
import com.springsecurity.supportportal.exceptions.UsernameExistsException;
import com.springsecurity.supportportal.exceptions.domains.EmailExistsException;
import com.springsecurity.supportportal.exceptions.domains.EmailNotFoundException;
import com.springsecurity.supportportal.exceptions.domains.UserNotFoundException;
import com.springsecurity.supportportal.repositories.UserRepository;
import com.springsecurity.supportportal.services.EmailService;
import com.springsecurity.supportportal.services.LoginAttemptsService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

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

    private UserRepository userRepository;
    private BCryptPasswordEncoder bcryptPasswordEncoder;
    private LoginAttemptsService loginAttemptsService;
    private EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bcryptPasswordEncoder,
            LoginAttemptsService loginAttemptsService, EmailService emailService) {
        this.userRepository = userRepository;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
        this.loginAttemptsService = loginAttemptsService;
        this.emailService = emailService;
    }

    // Is called whenever Spring Security is trying to check the authentication of
    // the User credentials.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            LOGGER.error(NO_USER_FOUND_BY_SURNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_SURNAME + username);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info("Returning found User by Username: " + username);
            return userPrincipal;
        }
    }

    private void validateLoginAttempt(User user) {
        if (user.isIsNotLocked()) {
            if (loginAttemptsService.hasExceededMaxAttempts(user.getUsername())) {
                user.setIsNotLocked(false);
            } else {
                user.setIsNotLocked(true);
            }
        } else {
            loginAttemptsService.evictUserFromLoginCache(user.getUsername());
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email)
            throws UserNotFoundException, UsernameExistsException, EmailExistsException, AddressException,
            MessagingException {
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
        user.setProfieImageUrl(getTemporalyProfileUrl(username));
        userRepository.save(user);
        LOGGER.info("New user password " + password);
        emailService.sendNewPasswordEmail(firstName, password, email);
        return user;
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

    @Override
    public User addNewUser(String firstName, String lastName, String username, String email, String role,
            boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws IOException {
        try {
            validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
            User user = new User();
            String password = generatePassword();
            String encodedPassword = encodedPassword(password);
            user.setUserId(generateUserId());
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setJoinedDate(new Date());
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(encodedPassword);
            user.setIsActive(isActive);
            user.setIsNotLocked(isNotLocked);
            user.setRole(getRoleEnumName(role).name());
            user.setAuthorities(getRoleEnumName(role).getAuthorities());
            user.setProfieImageUrl(getTemporalyProfileUrl(username));
            userRepository.save(user);
            saveProfileImage(user, profileImage);
            return user;
        } catch (UserNotFoundException | UsernameExistsException | EmailExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername,
            String newEmail, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws IOException {
        try {
            User currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
            currentUser.setFirstName(newFirstName);
            currentUser.setLastName(newLastName);
            currentUser.setUsername(newUsername);
            currentUser.setEmail(newEmail);
            currentUser.setIsActive(isActive);
            currentUser.setIsNotLocked(isNotLocked);
            currentUser.setRole(getRoleEnumName(role).name());
            currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
            userRepository.save(currentUser);
            saveProfileImage(currentUser, profileImage);
            return currentUser;
        } catch (UserNotFoundException | UsernameExistsException | EmailExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    // When refactoring,
    // Make this method to delete User by userId or username
    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException, AddressException, MessagingException {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL);
        }

        String password = generatePassword();
        user.setPassword(encodedPassword(password));
        emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());

    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage)
            throws UserNotFoundException, UsernameExistsException, EmailExistsException, IOException {

        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, profileImage);

        return user;
    }

    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
        if(profileImage != null){
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if(Files.exists(userFolder)){
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
        user.setProfieImageUrl(setProfileImageUrl(user.getUsername()));
        userRepository.save(user);
        LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    private String setProfileImageUrl(String username) {
        return  ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION)
        .toUriString();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private String getTemporalyProfileUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username)
                .toUriString();
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

}
