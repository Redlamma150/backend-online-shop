package com.example.security.repository;

import com.example.security.model.Order;
import com.example.security.model.OrderItem;
import com.example.security.repository.mapper.OrderItemMapper;
import com.example.security.repository.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TABLE1 = "orders";
    private static final String TABLE2 = "order_items";

    // ===== יצירה / שליפה של הזמנת TEMP =====

    // יצירת הזמנה חדשה TEMP ומחזירה את ה-ID
    public Long createTempOrder(Long userId) {
        String insert = "INSERT INTO " + TABLE1 + " (user_id, date, status) VALUES (?, CURRENT_TIMESTAMP, 'TEMP')";
        jdbcTemplate.update(insert, userId);

        // H2: עדיף FETCH FIRST 1 ROWS ONLY (עובד גם בלוקאלית)
        String getIdSql =
                "SELECT id FROM " + TABLE1 +
                        " WHERE user_id = ? AND status = 'TEMP' ORDER BY date DESC FETCH FIRST 1 ROWS ONLY";
        return jdbcTemplate.queryForObject(getIdSql, Long.class, userId);
    }

    // שליפת הזמנה זמנית של משתמש
    public Long getTempOrderId(Long userId) {
        try {
            String sql =
                    "SELECT id FROM " + TABLE1 +
                            " WHERE user_id = ? AND status = 'TEMP' FETCH FIRST 1 ROWS ONLY";
            return jdbcTemplate.queryForObject(sql, Long.class, userId);
        } catch (Exception e) {
            return null;
        }
    }

    // ===== פריטים בהזמנה (הוספה / הפחתה / הסרה) =====

    public String addItemToOrder(Long orderId, Long itemId) {
        String checkSql = "SELECT COUNT(*) FROM " + TABLE2 + " WHERE order_id = ? AND item_id = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, orderId, itemId);

        if (count > 0) {
            String update = "UPDATE " + TABLE2 + " SET quantity = quantity + 1 WHERE order_id = ? AND item_id = ?";
            jdbcTemplate.update(update, orderId, itemId);
            return "Quantity updated in order.";
        } else {
            String priceSql = "SELECT price FROM items WHERE id = ?";
            Double unitPrice = jdbcTemplate.queryForObject(priceSql, Double.class, itemId);

            String insertSql = "INSERT INTO " + TABLE2 + " (order_id, item_id, quantity, unit_price) VALUES (?, ?, 1, ?)";
            jdbcTemplate.update(insertSql, orderId, itemId, unitPrice);
            return "Item added to order.";
        }
    }

    public Integer getItemQuantity(Long orderId, Long itemId) {
        String sql = "SELECT quantity FROM " + TABLE2 + " WHERE order_id = ? AND item_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, orderId, itemId);
        } catch (Exception e) {
            return null;
        }
    }

    public int decreaseItemQuantity(Long orderId, Long itemId) {
        String sql = "UPDATE " + TABLE2 + " SET quantity = quantity - 1 WHERE order_id = ? AND item_id = ? AND quantity > 1";
        return jdbcTemplate.update(sql, orderId, itemId);
    }

    public Integer getItemRemaining(Long itemId) {
        String sql = "SELECT COALESCE(stock, 0) FROM items WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, itemId);
        } catch (Exception e) {
            return 0; // אם אין פריט, מחזיר 0 כדי לא להפיל את הבקשה
        }
    }



    public String removeItemFromOrder(Long orderId, Long itemId) {
        String sql = "DELETE FROM " + TABLE2 + " WHERE order_id = ? AND item_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, orderId, itemId);
        return rowsAffected > 0 ? "Item removed from order." : "Item not found in order.";
    }

    public boolean isOrderEmpty(Long orderId) {
        String sql = "SELECT COUNT(*) FROM " + TABLE2 + " WHERE order_id = ?";
        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, orderId);
        return cnt == null || cnt == 0;
    }

    // ===== מחיקה של הזמנות =====

    // מחיקת הזמנות TEMP בלבד של המשתמש (כולל הפריטים) — H2: בלי JOIN
    public int deleteTempOrderByUserId(Long userId) {
        String delItems =
                "DELETE FROM " + TABLE2 + " WHERE order_id IN (" +
                        "  SELECT id FROM " + TABLE1 + " WHERE user_id = ? AND status = 'TEMP'" +
                        ")";
        jdbcTemplate.update(delItems, userId);

        String delOrder = "DELETE FROM " + TABLE1 + " WHERE user_id = ? AND status = 'TEMP'";
        return jdbcTemplate.update(delOrder, userId);
    }

    // מחיקה גורפת של כל ההזמנות (TEMP + CLOSE) של המשתמש — H2: בלי JOIN
    public int deleteAllOrdersByUserId(Long userId) {
        String delItems =
                "DELETE FROM " + TABLE2 + " WHERE order_id IN (" +
                        "  SELECT id FROM " + TABLE1 + " WHERE user_id = ?" +
                        ")";
        jdbcTemplate.update(delItems, userId);

        String delOrders = "DELETE FROM " + TABLE1 + " WHERE user_id = ?";
        return jdbcTemplate.update(delOrders, userId);
    }

    // מחיקת הזמנה סגורה לפי מזהה — כולל פריטים
    public int deleteClosedOrderById(Long orderId) {
        String delItems = "DELETE FROM " + TABLE2 + " WHERE order_id = ?";
        jdbcTemplate.update(delItems, orderId);

        String delOrder = "DELETE FROM " + TABLE1 + " WHERE id = ? AND status = 'CLOSE'";
        return jdbcTemplate.update(delOrder, orderId);
    }

    // בעלים של הזמנה סגורה (לצורכי הרשאה)
    public Long getOrderOwnerIfClosed(Long orderId) {
        try {
            String sql = "SELECT user_id FROM " + TABLE1 + " WHERE id = ? AND status = 'CLOSE'";
            return jdbcTemplate.queryForObject(sql, Long.class, orderId);
        } catch (Exception e) {
            return null;
        }
    }

    // מוריד מלאי לכל הפריטים שבהזמנה
    public int applyStockForClosedOrder(Long orderId) {
        String sql = """
        UPDATE items i
           SET stock = CASE
                           WHEN stock >= (
                               SELECT oi.quantity FROM order_items oi
                               WHERE oi.order_id = ? AND oi.item_id = i.id
                           ) THEN stock - (
                               SELECT oi.quantity FROM order_items oi
                               WHERE oi.order_id = ? AND oi.item_id = i.id
                           )
                           ELSE 0
                       END
         WHERE EXISTS (
               SELECT 1 FROM order_items oi
               WHERE oi.order_id = ? AND oi.item_id = i.id
         )
        """;
        // אותו orderId נדרש 3 פעמים
        return jdbcTemplate.update(sql, orderId, orderId, orderId);
    }


    // ===== שליפות =====

    public List<Order> getOrdersByUserId(Long userId) {
        String sql = "SELECT * FROM " + TABLE1 + " WHERE user_id = ? ORDER BY date DESC";
        return jdbcTemplate.query(sql, new OrderMapper(), userId);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        String sql = """
        SELECT oi.id, oi.order_id, oi.item_id, oi.quantity, oi.unit_price,
               i.title, i.image_url, i.stock AS remaining_stock
          FROM order_items oi
          JOIN items i ON oi.item_id = i.id
         WHERE oi.order_id = ?
        """;
        return jdbcTemplate.query(sql, new OrderItemMapper(), orderId);
    }

    public String markOrderAsClosed(Long orderId, String address, double totalPrice) {
        String sql = "UPDATE " + TABLE1 + " SET status = 'CLOSE', address = ?, total_price = ? WHERE id = ?";
        jdbcTemplate.update(sql, address, totalPrice, orderId);
        return "Order completed successfully";
    }

    public Order getClosedOrderById(Long orderId) {
        try {
            String sql = "SELECT * FROM " + TABLE1 + " WHERE id = ? AND status = 'CLOSE'";
            return jdbcTemplate.queryForObject(sql, new OrderMapper(), orderId);
        } catch (Exception e) {
            return null;
        }
    }

    public double calculateTotalPrice(Long orderId) {
        String sql = "SELECT SUM(quantity * unit_price) FROM " + TABLE2 + " WHERE order_id = ?";
        Double total = jdbcTemplate.queryForObject(sql, Double.class, orderId);
        return total != null ? total : 0.0;
    }
}
