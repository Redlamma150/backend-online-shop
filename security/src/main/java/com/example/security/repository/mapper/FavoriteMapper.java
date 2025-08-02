package com.example.security.repository.mapper;

import com.example.security.model.Favorite;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FavoriteMapper implements RowMapper<Favorite> {
    @Override
    public Favorite mapRow(ResultSet rs, int rowNum) throws SQLException {
        Favorite favorite = new Favorite();
        favorite.setId(rs.getLong("id"));
        favorite.setUserId(rs.getLong("user_id"));
        favorite.setItemId(rs.getLong("item_id"));
        return favorite;
    }
}
