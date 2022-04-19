package com.springsecurity.supportportal.domains;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

//This class coverts the normal User to a Spring Security User so it can be authenticated.
public class UserPrincipal implements UserDetails {

    private User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

       // return this.user.getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
       return Stream.of(user.getAuthorities()).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public User getUser(){
        return user;
    }

    @Override
    public String getPassword() {

        return this.user.getPassword();
    }

    @Override
    public String getUsername() {

        return this.user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {

        return this.user.isIsNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {

        return this.user.isIsActive();
    }

}
