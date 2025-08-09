package com.example.security.repository;

import com.example.security.model.Favorite;
import com.example.security.repository.mapper.FavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FavoriteRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // אם שם הטבלה אצלך הוא "favorites" — עדכן פה:
    private static final String TABLE = "favorite";

    public List<Favorite> findByUserId(Long userId){
        String sql = String.format("SELECT * FROM %s WHERE user_id = ?", TABLE);
        return jdbcTemplate.query(sql, new FavoriteMapper(), userId);
    }

    public String addFavorite(Long userId, Long itemId) {
        if (exists(userId, itemId)) {
            return "This item is already in favorites.";
        }
        String sql = String.format("INSERT INTO %s (user_id, item_id) VALUES (?, ?)", TABLE);
        jdbcTemplate.update(sql, userId, itemId);
        return "The product has been added successfully to favorites";
    }

    public String removeFavorite(Long userId, Long itemId){
        String sql = String.format("DELETE FROM %s WHERE user_id = ? AND item_id = ?", TABLE);
        int rows = jdbcTemplate.update(sql, userId, itemId);
        if (rows == 0) {
            return "Item not found in favorites";
        }
        return "The item has been removed successfully from favorites";
    }

    public boolean exists(Long userId, Long itemId){
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE user_id = ? AND item_id = ?", TABLE);
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, itemId);
        return count != null && count > 0;
    }

    public String deleteByUserId(Long userId){
        String sql = String.format("DELETE FROM %s WHERE user_id = ?", TABLE);
        jdbcTemplate.update(sql, userId);
        return "Favorites deleted successfully for user";
    }
}
