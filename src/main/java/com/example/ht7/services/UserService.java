package com.example.ht7.services;

import com.example.ht7.entities.Roles;
import com.example.ht7.entities.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    Users getUserByEmail(String email);
    Users createUser(Users user);
    List<Users> getAllUsers();
    Users getUserById(Long id);
    Users saveUser(Users user);
    void deleteUser(Users user);

    List<Roles> getAllRoles();
    Roles getRole(Long id);
}
