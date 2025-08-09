package com.example.security.service;

import com.example.security.model.Order;
import com.example.security.model.OrderItem;
import com.example.security.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // הוספת פריט להזמנה זמנית של המשתמש (+1)
    public void addItemToPendingOrder(Long userId, Long itemId) {
        Long orderId = orderRepository.getTempOrderId(userId);
        if (orderId == null) {
            orderId = orderRepository.createTempOrder(userId);
        }
        orderRepository.addItemToOrder(orderId, itemId);
    }

    // הפחתת כמות פריט אחד (−1). אם הכמות הייתה 1 → מחיקה מההזמנה; אם הזמנה התרוקנה → מחיקת TEMP
    public void decreaseItemQuantityOrRemove(Long userId, Long itemId) {
        Long orderId = orderRepository.getTempOrderId(userId);
        if (orderId == null) return;

        Integer qty = orderRepository.getItemQuantity(orderId, itemId);
        if (qty == null) return;

        if (qty > 1) {
            orderRepository.decreaseItemQuantity(orderId, itemId);
        } else {
            orderRepository.removeItemFromOrder(orderId, itemId);
            if (orderRepository.isOrderEmpty(orderId)) {
                orderRepository.deleteTempOrderByUserId(userId); // מוחק TEMP בלבד
            }
        }
    }

    public Integer getItemRemaining(Long itemId) {
        return orderRepository.getItemRemaining(itemId);
    }

    public Long getTempOrderId(Long userId) {
        return orderRepository.getTempOrderId(userId);
    }

    // הסרת פריט מההזמנה (מחיקה מלאה של אותו פריט); אם הזמנה התרוקנה → מחיקת TEMP
    public void removeItemFromPendingOrder(Long userId, Long itemId) {
        Long orderId = orderRepository.getTempOrderId(userId);
        if (orderId == null) return;

        orderRepository.removeItemFromOrder(orderId, itemId);

        if (orderRepository.isOrderEmpty(orderId)) {
            orderRepository.deleteTempOrderByUserId(userId); // מוחק TEMP בלבד
        }
    }

    // ביטול מלא של הזמנת TEMP (ניקוי העגלה)
    public void cancelTempOrder(Long userId) {
        orderRepository.deleteTempOrderByUserId(userId); // מוחק TEMP בלבד
    }

    // סיום הזמנה זמנית - סוגר אותה ומעדכן כתובת וסכום סופי
    public String completeOrder(Long userId, String address) {
        Long orderId = orderRepository.getTempOrderId(userId);
        if (orderId == null) return "No pending order to complete";

        double totalPrice = orderRepository.calculateTotalPrice(orderId);

        // קודם מעדכנים מלאי לפי הכמויות שהוזמנו
        orderRepository.applyStockForClosedOrder(orderId);

        // ואז סוגרים את ההזמנה ל‑CLOSE
        return orderRepository.markOrderAsClosed(orderId, address, totalPrice);
    }

    // שליפת כל ההזמנות של המשתמש
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.getOrdersByUserId(userId);
    }

    // שליפת פריטים לפי מזהה הזמנה
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderRepository.getOrderItems(orderId);
    }

    // שליפת הזמנה סגורה לפי מזהה
    public Order getClosedOrder(Long orderId) {
        return orderRepository.getClosedOrderById(orderId);
    }

    // === מחיקות אופציונליות ===

    // מחיקה גורפת של כל ההזמנות (TEMP + CLOSE)
    public void deleteAllOrdersByUserId(Long userId) {
        orderRepository.deleteAllOrdersByUserId(userId);
    }

    // מחיקה של הזמנות TEMP בלבד
    public void deleteTempOrdersByUserId(Long userId) {
        orderRepository.deleteTempOrderByUserId(userId);
    }

    // מחיקת הזמנה סגורה לפי מזהה, רק אם שייכת למשתמש
    public void deleteClosedOrderById(Long userId, Long orderId) {
        Long ownerId = orderRepository.getOrderOwnerIfClosed(orderId);
        if (ownerId == null) return;           // אין הזמנה סגורה כזו
        if (!ownerId.equals(userId)) return;   // לא שייך למשתמש
        orderRepository.deleteClosedOrderById(orderId);
    }
}
