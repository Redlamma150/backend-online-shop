package com.example.security.service;

import com.example.security.model.CustomUser;
//import com.example.security.model.Role;
//import com.example.security.model.Role;
import com.example.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(CustomUser user) {
        if (user.getFirstName() == null || user.getLastName() == null || user.getEmail() == null
                || user.getUsername() == null || user.getPassword() == null) {
            return "User not created, first name, last name, email, username and password are required";
        }
        CustomUser userWithTheSameEmail = getUserByEmail(user.getEmail());
        CustomUser userWithTheSameUsername = getUserByUsername(user.getUsername());
        if(
                userWithTheSameEmail != null || userWithTheSameUsername != null){
            return String.format("User not created. The %s already exists.",
                    userWithTheSameEmail != null ? "email" : "username");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println("Encoded password: " + user.getPassword()); // Log the encoded password

        return userRepository.register(user);
    }

    public CustomUser getUserByEmail(String email) {
        try {
            return userRepository.findUserByEmail(email);
        } catch (Exception e) {
            return null;
        }
    }

    public CustomUser getUserByUsername(String username) {
        try {
            return userRepository.findUserByUsername(username);
        } catch (Exception e) {
            return null;
        }
    }


    public List<CustomUser> getAllUsers() {
        return userRepository.findAllUsers();
    }

    public CustomUser updateUser(CustomUser updatedUser) {
        return userRepository.updateUser(updatedUser);
    }

    public String deleteUser(String username) {
        CustomUser registeredUser = userRepository.findUserByUsername(username);
        if (registeredUser == null) {
            return "The user with this username does not exist, so it cannot be deleted";
        }
        return userRepository.deleteUser(registeredUser.getUsername());
    }
    public String changePassword(Long userId, String oldPassword, String newPassword) {
        return userRepository.changePassword(userId, oldPassword, newPassword);
    }


}

