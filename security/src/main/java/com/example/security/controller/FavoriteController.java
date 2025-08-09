package com.example.security.controller;


import com.example.security.model.CustomUser;
import com.example.security.model.Favorite;
import com.example.security.service.FavoriteService;
import com.example.security.service.UserService;
import com.example.security.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "http://localhost:3000")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/{itemId}")
    public ResponseEntity<String> addFavorite(@RequestHeader("Authorization") String token,
                                              @PathVariable Long itemId) {
        try {
            Long userId = extractUserIdFromToken(token);
            String result = favoriteService.addFavorite(userId, itemId);
            if (result.contains("successfully")) {
                return ResponseEntity.status(HttpStatus.CREATED).body(result); // 201
            } else if (result.toLowerCase().contains("already")) {
                return ResponseEntity.ok(result); // 200 במקום 400
            } else {
                return ResponseEntity.badRequest().body(result); // כל השאר
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Favorite>> getFavorites(@RequestHeader("Authorization") String token) {
        try {
            Long userId = extractUserIdFromToken(token);
            System.out.println("Looking for favorites for userId: " + userId);  // DEBUG
            List<Favorite> favorites = favoriteService.getFavoritesByUserId(userId);
            return new ResponseEntity<>(favorites, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();  // חשוב
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> removeFavorite(@RequestHeader("Authorization") String token,
                                                 @PathVariable Long itemId) {
        try {
            Long userId = extractUserIdFromToken(token);
            String result = favoriteService.removeFavorite(userId, itemId);
            if (result.toLowerCase().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result); // 404
            }
            return ResponseEntity.ok(result); // 200
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/exists/{itemId}")
    public ResponseEntity<Boolean> isFavorite(@RequestHeader("Authorization") String token,
                                              @PathVariable Long itemId) {
        try {
            Long userId = extractUserIdFromToken(token);
            return new ResponseEntity<>(favoriteService.isFavorite(userId, itemId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Long extractUserIdFromToken(String token) {
        String jwt = token.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        CustomUser user = userService.getUserByUsername(username);
        System.out.println("Extracted userId: " + user.getId());
        return user.getId();
    }
}
