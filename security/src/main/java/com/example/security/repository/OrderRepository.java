package com.example.security.repository;

import com.example.security.model.Order;
import com.example.security.model.OrderItem;
import com.example.security.model.Status;
import com.example.security.repository.mapper.OrderItemMapper;
import com.example.security.repository.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class OrderRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TABLE1= "orders";
    private static final String TABLE2= "order_items";

    // יצירת הזמנה חדשה TEMP
    public Long createTempOrder(Long userId) {
        String sql = "INSERT INTO orders (user_id, date, status) VALUES (?, NOW(), 'TEMP')";
        jdbcTemplate.update(sql, userId);

        String getIdSql = "SELECT id FROM orders WHERE user_id = ? AND status = 'TEMP' ORDER BY date DESC LIMIT 1";
        return jdbcTemplate.queryForObject(getIdSql, Long.class, userId);
    }



    // שליפת הזמנה זמנית של משתמש
    public Long getTempOrderId(Long userId) {
        try {
            String sql = String.format("SELECT id FROM %s WHERE user_id = ? AND status = 'TEMP' LIMIT 1", TABLE1);
            return jdbcTemplate.queryForObject(sql, Long.class, userId);
        } catch (Exception e) {
            System.out.println("No pending order found: " + e.getMessage());
            return null;
        }
    }

    // הוספת פריט להזמנה TEMP (אם כבר קיים - עדכון כמות)
    public String addItemToOrder(Long orderId, Long itemId) {
        System.out.println(">>> addItemToOrder: orderId = " + orderId + ", itemId = " + itemId);


        String checkSql = String.format("SELECT COUNT(*) FROM %s WHERE order_id = ? AND item_id = ?", TABLE2);
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, orderId, itemId);

        if (count > 0) {
            String update = String.format("UPDATE %s SET quantity = quantity + 1 WHERE order_id = ? AND item_id = ?", TABLE2);
            jdbcTemplate.update(update, orderId, itemId);
            return "Quantity updated in order.";
        } else {
            String priceSql = "SELECT price FROM items WHERE id = ?";
            Double unitPrice = jdbcTemplate.queryForObject(priceSql, Double.class, itemId);

            String insertSql = String.format("INSERT INTO %s (order_id, item_id, quantity, unit_price) VALUES (?, ?, 1, ?)", TABLE2);
            jdbcTemplate.update(insertSql, orderId, itemId, unitPrice);
            return "Item added to order.";
        }
    }


    // בדיקה אם הזמנה ריקה
    public boolean isOrderEmpty(Long orderId) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE order_id = ?", TABLE2);
        return jdbcTemplate.queryForObject(sql, Integer.class, orderId) == 0;
    }

    // מחיקת הזמנה
    public String deleteByUserId(Long userId){
        String sql = String.format("DELETE FROM %s WHERE user_id = ?", TABLE1);
        jdbcTemplate.update(sql, userId);
        return "The order has been deleted successfully";
    }
    // מחיקת פריט מתוך הזמנה
    public String removeItemFromOrder(Long orderId, Long itemId) {
        String sql = String.format("DELETE FROM %s WHERE order_id = ? AND item_id = ?", TABLE2);
        int rowsAffected = jdbcTemplate.update(sql, orderId, itemId);
        return rowsAffected > 0 ? "Item removed from order." : "Item not found in order.";
    }

    // שליפת כל ההזמנות של משתמש
    public List<Order> getOrdersByUserId(Long userId) {
        String sql = String.format("SELECT * FROM %s WHERE user_id = ? ORDER BY date DESC", TABLE1);
        return jdbcTemplate.query(sql, new OrderMapper(), userId);
    }

    // שליפת פריטים לפי הזמנה
    public List<OrderItem> getOrderItems(Long orderId) {
        String sql = """
        SELECT oi.id, oi.order_id, oi.item_id, oi.quantity, oi.unit_price,
               i.title, i.image_url
        FROM order_items oi
        JOIN items i ON oi.item_id = i.id
        WHERE oi.order_id = ?
        """;
        return jdbcTemplate.query(sql, new OrderItemMapper(), orderId);
    }

    // סגירת הזמנה TEMP (שינוי סטטוס ל־CLOSE + כתובת + מחיר כולל)
    public String markOrderAsClosed(Long orderId, String address, double totalPrice) {
        String sql = String.format("UPDATE %s SET status = 'CLOSE', address = ?, total_price = ? WHERE id = ?",TABLE1);
        jdbcTemplate.update(sql, address, totalPrice, orderId);
        return "Order completed successfully";
    }

    // שליפת הזמנה סגורה לפי ID
    public Order getClosedOrderById(Long orderId) {
        try {
            String sql = "SELECT * FROM orders WHERE id = ? AND status = 'CLOSE'";
            return jdbcTemplate.queryForObject(sql, new OrderMapper(), orderId);
        } catch (Exception e) {
            return null;
        }

    }

    public double calculateTotalPrice(Long orderId) {
        String sql = "SELECT SUM(quantity * unit_price) FROM order_items WHERE order_id = ?";
        Double total = jdbcTemplate.queryForObject(sql, Double.class, orderId);
        return total != null ? total : 0.0;
    }

}
