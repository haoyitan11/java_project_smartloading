package com.assignment.smartloading.repository;

import com.assignment.smartloading.model.LoginUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginUserRepository extends JpaRepository<LoginUser, String> {

    LoginUser findByUsername(String username);
}
