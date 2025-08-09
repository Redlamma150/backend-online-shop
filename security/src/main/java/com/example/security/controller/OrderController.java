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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(
        origins = "http://localhost:3000",
        allowedHeaders = {"Authorization", "Content-Type"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class OrderController {

    @Autowired private OrderService orderService;
    @Autowired private UserService userService;
    @Autowired private JwtUtil jwtUtil;

    // הוספת פריט להזמנה זמנית (+1)
    @PostMapping("/add/{itemId}")
    public ResponseEntity<Map<String, Integer>> addItem(@RequestHeader("Authorization") String auth,
                                                        @PathVariable Long itemId) {

        Long userId = extractUserIdFromToken(auth);
        // הוספה להזמנה
        orderService.addItemToPendingOrder(userId, itemId);
        // שליפת יתרה מהמאגר
        Integer remaining = orderService.getItemRemaining(itemId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("remaining", remaining != null ? remaining : 0));
    }

    // הפחתה ב-1; אם הכמות הייתה 1 -> מחיקה
    @PutMapping("/decrease/{itemId}")
    public ResponseEntity<Void> decreaseItem(@RequestHeader("Authorization") String auth,
                                             @PathVariable Long itemId) {
        Long userId = extractUserIdFromToken(auth);
        orderService.decreaseItemQuantityOrRemove(userId, itemId);
        return ResponseEntity.noContent().build(); // 204
    }

    // שליפת פריטי ההזמנה הזמנית (TEMP) של המשתמש
    @GetMapping("/temp/items")
    public ResponseEntity<List<OrderItem>> getTempOrderItems(@RequestHeader("Authorization") String auth) {
        Long userId = extractUserIdFromToken(auth);
        Long orderId = orderService.getTempOrderId(userId); // נוסיף מתודה חשופה לשירות (ראה למטה)
        if (orderId == null) {
            return ResponseEntity.ok(List.of()); // עגלה ריקה מחזירים []
        }
        return ResponseEntity.ok(orderService.getOrderItems(orderId));
    }


    // הסרת פריט מההזמנה (מחיקה מלאה)
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<Void> removeItem(@RequestHeader("Authorization") String auth,
                                           @PathVariable Long itemId) {
        Long userId = extractUserIdFromToken(auth);
        orderService.removeItemFromPendingOrder(userId, itemId);
        return ResponseEntity.noContent().build(); // 204
    }

    // ביטול מלא של הזמנת TEMP (ניקוי עגלה)
    @DeleteMapping("/temp")
    public ResponseEntity<Void> cancelTemp(@RequestHeader("Authorization") String auth) {
        Long userId = extractUserIdFromToken(auth);
        orderService.cancelTempOrder(userId);
        return ResponseEntity.noContent().build(); // 204
    }

    // סגירת הזמנה זמנית (ל־CLOSE)
    @PutMapping("/complete")
    public ResponseEntity<String> completeOrder(@RequestHeader("Authorization") String auth,
                                                @RequestParam String address) {
        Long userId = extractUserIdFromToken(auth);
        String result = orderService.completeOrder(userId, address);
        return ResponseEntity.ok(result);
    }

    // שליפת כל ההזמנות של המשתמש
    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(@RequestHeader("Authorization") String auth) {
        Long userId = extractUserIdFromToken(auth);
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    // שליפת כל הפריטים לפי מזהה הזמנה
    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderItems(orderId));
    }

    // שליפת הזמנה סגורה לפי מזהה
    @GetMapping("/closed/{orderId}")
    public ResponseEntity<Order> getClosedOrder(@PathVariable Long orderId) {
        Order order = orderService.getClosedOrder(orderId);
        return (order != null) ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    // מחיקת הזמנה סגורה לפי מזהה (רק אם שייכת למשתמש)
    @DeleteMapping("/closed/{orderId}")
    public ResponseEntity<Void> deleteClosedOrder(@RequestHeader("Authorization") String auth,
                                                  @PathVariable Long orderId) {
        Long userId = extractUserIdFromToken(auth);
        orderService.deleteClosedOrderById(userId, orderId);
        return ResponseEntity.noContent().build(); // 204
    }

    private Long extractUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }
        String jwt = authHeader.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        CustomUser user = userService.getUserByUsername(username);
        return user.getId();
    }
}
