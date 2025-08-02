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

    // הוספת פריט להזמנה זמנית של המשתמש
    public String addItemToPendingOrder(Long userId, Long itemId) {
        System.out.println(">>> addItemToPendingOrder: userId = " + userId + ", itemId = " + itemId);
        Long orderId = orderRepository.getTempOrderId(userId);
        if (orderId == null) {
            System.out.println(">>> No TEMP order found, creating new...");
            orderId = orderRepository.createTempOrder(userId);
            System.out.println(">>> Created new TEMP order with ID: " + orderId);
        }
        String result = orderRepository.addItemToOrder(orderId, itemId);
        System.out.println(">>> Result of addItemToOrder: " + result);
        return result;
    }

    public String removeItemFromPendingOrder(Long userId, Long itemId) {
        System.out.println(">>> removeItemFromPendingOrder: userId = " + userId + ", itemId = " + itemId);
        Long orderId = orderRepository.getTempOrderId(userId);
        if (orderId == null) {
            return "No pending order found.";
        }

        String removeResult = orderRepository.removeItemFromOrder(orderId, itemId);
        System.out.println(">>> Remove result: " + removeResult);

        if (orderRepository.isOrderEmpty(orderId)) {
            String deleteResult = orderRepository.deleteByUserId(userId);
            System.out.println(">>> Order was empty, deleted: " + deleteResult);
            return removeResult + " " + deleteResult;
        }

        return removeResult;
    }

    public String deleteOrdersByUserId(Long userId) {
        return orderRepository.deleteByUserId(userId);
    }


    // סיום הזמנה זמנית - סוגר אותה ומעדכן כתובת וסכום סופי
    public String completeOrder(Long userId, String address) {

        Long orderId = orderRepository.getTempOrderId(userId);
        if (orderId == null) return "No pending order to complete";

        double totalPrice = orderRepository.calculateTotalPrice(orderId);
        System.out.println(">>> Total price for order ID " + orderId + ": " + totalPrice);

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
}

