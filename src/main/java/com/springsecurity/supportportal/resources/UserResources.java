package com.springsecurity.supportportal.resources;

import static com.springsecurity.supportportal.constants.SecurityConstants.JWT_TOKEN_HEADER;
import static com.springsecurity.supportportal.constants.EmailConstants.EMAIL_SENT;
import static com.springsecurity.supportportal.constants.EmailConstants.USER_DELETED_SUCCESSFULLY;
import static com.springsecurity.supportportal.constants.FileConstants.USER_FOLDER;
import static com.springsecurity.supportportal.constants.FileConstants.FORWARD_SLASH;
import static com.springsecurity.supportportal.constants.FileConstants.TEMP_PROFILE_IMAGE_BASE_URL;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.springsecurity.supportportal.domains.HttpResponse;
import com.springsecurity.supportportal.domains.User;
import com.springsecurity.supportportal.domains.UserPrincipal;
import com.springsecurity.supportportal.utilities.JWTTokenProvider;
import com.springsecurity.supportportal.exceptions.UsernameExistsException;
import com.springsecurity.supportportal.exceptions.domains.EmailExistsException;
import com.springsecurity.supportportal.exceptions.domains.EmailNotFoundException;
import com.springsecurity.supportportal.exceptions.domains.ExceptionHandling;
import com.springsecurity.supportportal.exceptions.domains.UserNotFoundException;
import com.springsecurity.supportportal.resources.impl.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

/**
 * Figure out why the Email service is not working
 **/
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
            throws UserNotFoundException, UsernameExistsException, EmailExistsException, AddressException,
            MessagingException {
        User newUser = userServiceImpl.register(user.getFirstName(), user.getLastName(), user.getUsername(),
                user.getEmail());
        return new ResponseEntity<User>(newUser, HttpStatus.OK);

    }

    // Internally add a User if having privilegdes to do so.
    // Find a way to optimize this method.
    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName, @RequestParam("usename") String username,
            @RequestParam("email") String email, @RequestParam("role") String role,
            @RequestParam("isActive") String isActive, @RequestParam("isNonLocked") String isNonLocked,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {

        User newUser = userServiceImpl.addNewUser(firstName, lastName, username, email, role,
                Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);

        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<User> updateUser(@RequestParam("currentUsername") String currentUsername,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName, @RequestParam("usename") String username,
            @RequestParam("email") String email, @RequestParam("role") String role,
            @RequestParam("isActive") String isActive, @RequestParam("isNonLocked") String isNonLocked,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {

        User updatedUser = userServiceImpl.updateUser(
                currentUsername,
                firstName, lastName,
                username,
                email,
                role,
                Boolean.parseBoolean(isNonLocked),
                Boolean.parseBoolean(isActive),
                profileImage);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping("find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        User user = userServiceImpl.findUserByUsername(username);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userServiceImpl.getUsers();

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email)
            throws AddressException, EmailNotFoundException, MessagingException {
        userServiceImpl.resetPassword(email);

        return response(HttpStatus.OK, EMAIL_SENT + email);
    }

    // Since we Enabled "GlobalMethodSecurity" in SecurityConfiguration.java
    // We can configure security at method level.
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<HttpResponse> delete(@PathVariable("id") Long id) {
        userServiceImpl.deleteUser(id);

        return response(HttpStatus.NO_CONTENT, USER_DELETED_SUCCESSFULLY);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(
            @RequestParam("lastName") String lastName, @RequestParam("usename") String username,
            @RequestParam("profileImage") MultipartFile profileImage)
            throws IOException, UserNotFoundException, UsernameExistsException, EmailExistsException {

        User user = userServiceImpl.updateProfileImage(username, profileImage);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(path= "/image/{username}/{fileName}", produces= IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException{
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
    }

    @GetMapping(path= "/image/profile/{username}", produces= IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException{
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try( InputStream inputStream =  url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while((bytesRead = inputStream.read(chunk)) > 0){
               byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    private ResponseEntity<HttpResponse> response(HttpStatus status, String message) {
        HttpResponse httpResponse = new HttpResponse(
                status.value(),
                status, status.getReasonPhrase().toUpperCase(),
                message.toUpperCase());
        return new ResponseEntity<>(httpResponse, status);
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
