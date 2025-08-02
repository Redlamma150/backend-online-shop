package com.example.security.repository.mapper;

import com.example.security.model.Item;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemMapper implements RowMapper<Item> {
    @Override
    public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
        Item item = new Item();
        item.setId(rs.getLong("id"));
        item.setTitle(rs.getString("title"));
        item.setImageUrl(rs.getString("image_url"));
        item.setPrice(rs.getDouble("price"));
        item.setStock(rs.getInt("stock"));
        return item;
    }
}
