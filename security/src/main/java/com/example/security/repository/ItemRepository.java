package com.example.security.repository;

import com.example.security.model.Item;
import com.example.security.repository.mapper.ItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TABLE = "items";

    public List<Item> findAll(){
        String sql = String.format("SELECT * FROM %s", TABLE);
        return jdbcTemplate.query(sql, new ItemMapper());
    }
    public List<Item> searchByTitleIgnoreCase(String query) {
        String sql = String.format("SELECT * FROM %s WHERE LOWER(title) LIKE ?", TABLE);
        return jdbcTemplate.query(sql, new ItemMapper(),"%" + query.toLowerCase() + "%");
    }

    public Item findById(Long id) {
        try {
            String sql = String.format("SELECT * FROM %s WHERE id = ?", TABLE);
            return jdbcTemplate.queryForObject(sql, new ItemMapper(), id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void updateStock(Long productId, int newStock) {
        String sql = String.format("UPDATE %s SET stock = ? WHERE id = ?", TABLE);
        jdbcTemplate.update(sql, newStock, productId);
    }
}


