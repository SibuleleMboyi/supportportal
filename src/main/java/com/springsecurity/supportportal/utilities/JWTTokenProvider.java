package com.springsecurity.supportportal.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.springsecurity.supportportal.constants.SecurityConstants;
import com.springsecurity.supportportal.domains.UserPrincipal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component
public class JWTTokenProvider {
    @Value("${jwt.secret}")
    private String secret;

    // After the User login details have been verified, this method executes to
    // create a Token for this User.
    // ".withSubject()" takes a UNIQUE identifier of the User ( i.e USER ID)
    public String generateJwtToken(UserPrincipal UserPrincipal) {
        String[] claims = getClaimsFromUser(UserPrincipal);
        return JWT.create().withIssuer(SecurityConstants.GET_ARRAYS_LLC)
                .withAudience(SecurityConstants.GET_ARRAYS_ADMNISTRATION).withIssuedAt(new Date())
                .withSubject(UserPrincipal.getUsername()).withArrayClaim(SecurityConstants.AUTHORITIES, claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPERATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        ArrayList<String> claims = gtClaimsFromToken(token);
        return claims.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    // Once we have verified the Token,
    // This method gets the Authentication.
    // And we set it to the Spring Security Context, telling Spring that this User
    // is authenticated, process the request.
    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities,
            HttpServletRequest request) {
        UsernamePasswordAuthenticationToken userPasswordAuthToken = new UsernamePasswordAuthenticationToken(username,
                null, authorities);
        userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return userPasswordAuthToken;
    }

    public boolean isTokenValid(String username, String token) {
        JWTVerifier verifier = getJWTVerifier();
        return !username.isEmpty() && isTokenExperied(verifier, token);
    }

    private boolean isTokenExperied(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    public String getSubject(String token){
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }
    private ArrayList<String> gtClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return (ArrayList<String>) Arrays
                .asList(verifier.verify(token).getClaim(SecurityConstants.AUTHORITIES).asArray(String.class));
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier jwtVerifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            jwtVerifier = JWT.require(algorithm).withIssuer(SecurityConstants.GET_ARRAYS_LLC).build();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException(SecurityConstants.TOKEN_CANNOT_BE_VERIFIED);
        }
        return jwtVerifier;
    }

    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : userPrincipal.getAuthorities()) {
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }
}
