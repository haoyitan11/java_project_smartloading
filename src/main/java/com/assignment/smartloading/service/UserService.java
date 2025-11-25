package com.assignment.smartloading.service;

import com.assignment.smartloading.model.LoginUser;
import com.assignment.smartloading.repository.LoginUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {

    @Autowired
    private LoginUserRepository repo;

    public String validateUserAndReturnId(String username, String password) {

        // 1. Find user by username ONLY
        LoginUser user = repo.findByUsername(username);

        if (user == null)
            return null;

        // 2. Check password manually
        if (!user.getPassword().equals(password))  // later you can add hashing
            return null;

        // 3. Update last login
        user.setLastLogin(new Date());

        // OPTIONAL: generate session UUID for tracking
        String sessionId = java.util.UUID.randomUUID().toString();
        user.setSession(sessionId);

        repo.save(user);

        // 4. Always return true permanent user ID
        return user.getUserId();
    }
}
