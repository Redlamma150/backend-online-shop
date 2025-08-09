package com.example.security.repository.mapper;

import com.example.security.model.OrderItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemMapper implements RowMapper<OrderItem> {
    @Override
    public OrderItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderItem item = new OrderItem();
        item.setId(rs.getLong("id"));
        item.setOrderId(rs.getLong("order_id"));
        item.setItemId(rs.getLong("item_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getDouble("unit_price"));
        item.setTitle(rs.getString("title"));
        item.setImageUrl(rs.getString("image_url"));
        item.setRemainingStock(rs.getInt("remaining_stock"));
        return item;
    }
}
