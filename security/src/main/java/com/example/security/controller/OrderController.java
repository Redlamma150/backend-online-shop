package com.example.security.controller;

import com.example.security.model.CustomUser;
import com.example.security.model.Order;
import com.example.security.model.OrderItem;
import com.example.security.service.OrderService;
import com.example.security.service.UserService;
import com.example.security.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    //  הוספת פריט להזמנה זמנית
    @PostMapping("/add/{itemId}")
    public ResponseEntity<String> addItem(@RequestHeader("Authorization") String token,
                                          @PathVariable Long itemId) {
        try {
            Long userId = extractUserIdFromToken(token);
            orderService.addItemToPendingOrder(userId, itemId);
            return ResponseEntity.ok("Item added to pending order.");
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //סגירת הזמנה זמנית (הופכת ל-CLOSE)
    @PutMapping("/complete")
    public ResponseEntity<String> completeOrder(@RequestHeader("Authorization") String token,
                                                @RequestParam String address) {
        try {
            Long userId = extractUserIdFromToken(token);
            String result = orderService.completeOrder(userId, address);
            return ResponseEntity.ok(result);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //שליפת כל ההזמנות של המשתמש
    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(@RequestHeader("Authorization") String token) {
        try {
            Long userId = extractUserIdFromToken(token);
            return new ResponseEntity<>(orderService.getOrdersByUserId(userId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //שליפת כל הפריטים לפי מזהה הזמנה
    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable Long orderId) {
        try {
            return ResponseEntity.ok(orderService.getOrderItems(orderId));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //שליפת הזמנה סגורה לפי מזהה
    @GetMapping("/closed/{orderId}")
    public ResponseEntity<Order> getClosedOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.getClosedOrder(orderId);
            return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //  הסרת פריט מהזמנה זמנית
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<String> removeItem(@RequestHeader("Authorization") String token,
                                             @PathVariable Long itemId) {
        try {
            Long userId = extractUserIdFromToken(token);
            String result = orderService.removeItemFromPendingOrder(userId, itemId);
            return ResponseEntity.ok(result);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Long extractUserIdFromToken(String token) {
        String jwt = token.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        CustomUser user = userService.getUserByUsername(username);
        return user.getId();
    }
}
