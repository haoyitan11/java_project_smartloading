package com.assignment.smartloading.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "login_user")
public class LoginUser {

    @Id
    @Column(name = "user_id")
    private String userId;

    private String username;
    private String password;
    private String session;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_login")
    private Date lastLogin;

    public LoginUser() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSession() { return session; }
    public void setSession(String session) { this.session = session; }

    public Date getLastLogin() { return lastLogin; }
    public void setLastLogin(Date lastLogin) { this.lastLogin = lastLogin; }
}
