package com.example.security.repository.mapper;

import com.example.security.model.Order;
import com.example.security.model.Status;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper implements RowMapper<Order> {
    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setUserId(rs.getLong("user_id"));
        order.setDate(rs.getTimestamp("date").toLocalDateTime());
        order.setAddress(rs.getString("address"));
        order.setTotalPrice(rs.getDouble("total_price"));
        order.setStatus(Status.valueOf(rs.getString("status")));

        return order;
    }
}
