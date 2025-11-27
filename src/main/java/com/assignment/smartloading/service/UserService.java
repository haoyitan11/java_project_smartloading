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

        //find user by username ONLY
        LoginUser user = repo.findByUsername(username);

        if (user == null)
            return null;

        //check password with database
        if (!user.getPassword().equals(password))
            return null;

        //update last login date
        user.setLastLogin(new Date());

        //generate session UUID
        String sessionId = java.util.UUID.randomUUID().toString();
        user.setSession(sessionId);

        repo.save(user);

        //return user id
        return user.getUserId();
    }
}
