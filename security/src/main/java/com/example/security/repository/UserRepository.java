package com.example.security.repository;

import com.example.security.model.CustomUser;
import com.example.security.repository.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String USERS_TABLE = "users";

    public String register(CustomUser user) {
        String sql = String.format("INSERT INTO %s (first_name, last_name, email, phone, address, username, password) VALUES (?,?,?,?,?,?,?)", USERS_TABLE);
        jdbcTemplate.update(sql, user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone(), user.getAddress(), user.getUsername(), user.getPassword());
        return "User registered successfully";
    }

    public CustomUser findUserByUsername(String username) {
        String sql = String.format("SELECT * FROM %s WHERE username = ?", USERS_TABLE);
        List<CustomUser> users = jdbcTemplate.query(sql, new UserMapper(), username);
        return users.isEmpty() ? null : users.get(0);
    }

    public CustomUser findUserByEmail(String email) {
        String sql = String.format("SELECT * FROM %s WHERE email = ?", USERS_TABLE);
        List<CustomUser> users = jdbcTemplate.query(sql, new UserMapper(), email);
        return users.isEmpty() ? null : users.get(0);
    }


    public List<CustomUser> findAllUsers() {
        String sql = String.format("SELECT * FROM %s", USERS_TABLE);
        List<CustomUser> users = jdbcTemplate.query(sql, new UserMapper());
        return users;
    }

    public CustomUser updateUser(CustomUser user) {
        String sql = String.format("UPDATE %s SET first_name = ?, last_name = ?, email = ?, phone = ?, address = ? WHERE username = ?", USERS_TABLE);
        jdbcTemplate.update(sql, user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone(), user.getAddress(), user.getUsername());
        return findUserByUsername(user.getUsername());
    }

    public String deleteUser(String username) {
        String sql = String.format("DELETE FROM %s WHERE username = ?", USERS_TABLE);
        jdbcTemplate.update(sql, username);
        return "User deleted successfully";
    }

    public String changePassword (Long userId, String oldPassword, String newPassword){
        // שליפת הסיסמה הקיימת מהדאטהבייס
        String sql = String.format("SELECT password FROM %s WHERE id = ?", USERS_TABLE);
        String currentHashed = jdbcTemplate.queryForObject(sql, String.class, userId);

        // בדיקה שהסיסמה הישנה נכונה
        if (!passwordEncoder.matches(oldPassword, currentHashed)) {
            return "Incorrect current password.";
        }

        // הצפנת הסיסמה החדשה
        String newHashed = passwordEncoder.encode(newPassword);

        // עדכון בדאטהבייס
        String updateSql = String.format("UPDATE %s SET password = ? WHERE id = ?", USERS_TABLE);
        jdbcTemplate.update(updateSql, newHashed, userId);
        return "Password changed successfully";
    }

}
