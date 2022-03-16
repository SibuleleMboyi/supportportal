package com.springsecurity.supportportal.constants;

public class SecurityConstants {
    // 5 days expressed in milliseconds
    public static final long EXPERATION_TIME = 432000000;

    public static final String TOKEN_PREFIX = "Bearer";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";

    // Name of the Company or App
    public static final String GET_ARRAYS_LLC = "Get Arrays, LLC";

    public static final String GET_ARRAYS_ADMNISTRATION = "User Management Portal";
    public static final String AUTHORITIES = "Authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public static final String NO_USER_FOUND_BY_SURNAME = "No user found by username: ";
    public static final String DEFAULT_USER_IMAGE_PATH = "/user/image/profile/temp";
    // These URLs will be public, User does not need to be Authenticated to access
    // them
    // Reset password URL has stars because it will be a URL of the form ...
    // "/user/resetpassword/user_email"
    // public static final String[] PUBLIC_URLS = { "/user/login", "/user/register",
    // "/user/resetpassword/**","/user/image/**" };

    // Allows all requests
    public static final String[] PUBLIC_URLS = { "**" };

}
