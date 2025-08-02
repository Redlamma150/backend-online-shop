package com.example.security.controller;

import com.example.security.model.CustomUser;
import com.example.security.service.FavoriteService;
import com.example.security.service.OrderService;
import com.example.security.service.UserService;
import com.example.security.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private OrderService orderService;

    @PostMapping(value = "/api/register")
    public ResponseEntity<String> register(@RequestBody CustomUser user) {
        try {
            String result = userService.register(user);
            if (result.contains("successfully")) {
                return new ResponseEntity(result, HttpStatus.CREATED);
            }
            return new ResponseEntity(result, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<CustomUser> getUserByUsername(@RequestHeader(value = "Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            CustomUser user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping
    public ResponseEntity<CustomUser> updateUser(@RequestHeader(value = "Authorization") String token, @RequestBody CustomUser updatedUser) {
        try {
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            updatedUser.setUsername(username);
            if (updatedUser.getFirstName() == null || updatedUser.getLastName() == null || updatedUser.getEmail() == null) {
                return new ResponseEntity("User not updated, first name, last name and email are required", HttpStatus.BAD_REQUEST);
            }
            CustomUser userFromDB = userService.getUserByUsername(updatedUser.getUsername());
            if(!userFromDB.getEmail().equals(updatedUser.getEmail())){
                CustomUser userWithTheSameEmail = userService.getUserByEmail(updatedUser.getEmail());
                if(userWithTheSameEmail != null){
                    return new ResponseEntity("User not updated, This email already exist in the system.", HttpStatus.BAD_REQUEST);
                }
            }
            CustomUser user = userService.updateUser(updatedUser);
            if (user == null) {
                return new ResponseEntity("User not updated. this user does not exist in the system.", HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<String> deleteUser(@RequestHeader(value = "Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            CustomUser user = userService.getUserByUsername(username);
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            Long userId = user.getId();

            // 1. מחיקת פייבוריטים
            favoriteService.removeAllFavoriteByUserId(userId);

            // 2. מחיקת הזמנות
            orderService.deleteOrdersByUserId(userId);

            // 3. מחיקת המשתמש עצמו
            String result = userService.deleteUser(username);
            if (result.contains("successfully")) {
                return new ResponseEntity(result, HttpStatus.OK);
            }
            return new ResponseEntity(result, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String token,
                                                 @RequestBody Map<String, String> passwords) {
        Long userId = extractUserIdFromToken(token);
        String oldPass = passwords.get("old_password");
        String newPass = passwords.get("new_password");
        String result = userService.changePassword(userId, oldPass, newPass);
        return ResponseEntity.ok(result);
    }

    private Long extractUserIdFromToken(String token) {
        String jwt = token.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        CustomUser user = userService.getUserByUsername(username);
        return user.getId();
    }
}



