package com.springsecurity.supportportal.domains;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

//We implement Serializable because this class will go from being a Java Class into
//a different String that can be saved into the database.

@Entity
@Table(name = "users")
public class User implements Serializable {

    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    private String userId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String profieImageUrl;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinedDate;

    // Each User can have any role such as ROLE_ADMIN, ROLE_MANAGER.
    // And each role can have 1 or more authorities like read, write, update
    private String role;

    // Example: read, create, write, delete, update
    // private ArrayList<String> authorities = new ArrayList<String>();
    private String[] authorities;
    private boolean isActive;
    private boolean isNotLocked;

    public User() {
    }

    public User(Long id, String userId, String firstName, String lastName, String username, String password,
            String email, String profieImageUrl, Date lastLoginDate, Date lastLoginDateDisplay, Date joinedDate,
            String role, String[] authorities, boolean isActive, boolean isNotLocked) {
        this.id = id;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.profieImageUrl = profieImageUrl;
        this.lastLoginDate = lastLoginDate;
        this.lastLoginDateDisplay = lastLoginDateDisplay;
        this.joinedDate = joinedDate;
        this.role = role;
        this.authorities = authorities;
        this.isActive = isActive;
        this.isNotLocked = isNotLocked;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfieImageUrl() {
        return this.profieImageUrl;
    }

    public void setProfieImageUrl(String profieImageUrl) {
        this.profieImageUrl = profieImageUrl;
    }

    public Date getLastLoginDate() {
        return this.lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Date getLastLoginDateDisplay() {
        return this.lastLoginDateDisplay;
    }

    public void setLastLoginDateDisplay(Date lastLoginDateDisplay) {
        this.lastLoginDateDisplay = lastLoginDateDisplay;
    }

    public Date getJoinedDate() {
        return this.joinedDate;
    }

    public void setJoinedDate(Date joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String[] getAuthorities() {
        return this.authorities;
    }

    public void setAuthorities(String[] authorities) {
        this.authorities = authorities;
    }

    public boolean isIsActive() {
        return this.isActive;
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isIsNotLocked() {
        return this.isNotLocked;
    }

    public boolean getIsNotLocked() {
        return this.isNotLocked;
    }

    public void setIsNotLocked(boolean isNotLocked) {
        this.isNotLocked = isNotLocked;
    }

}
