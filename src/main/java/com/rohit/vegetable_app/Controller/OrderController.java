package com.rohit.vegetable_app.Controller;

import com.rohit.vegetable_app.Enum.OrderStatus;
import com.rohit.vegetable_app.Model.Order;
import com.rohit.vegetable_app.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // ✅ Constructor Injection (no @Autowired)
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ================================
    // ✅ PLACE ORDER
    // ================================
   // place order - for items in card
    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(
            @RequestParam String userId,
            @RequestParam String address) {

        return ResponseEntity.ok(orderService.placeOrder(userId, address));
    }

    // ================================
    // ❌ CANCEL ORDER
    // ================================
    @DeleteMapping("/cancel")
    public ResponseEntity<Order> cancelOrder(
            @RequestParam String orderId,
            @RequestParam String userId) {

        return ResponseEntity.ok(orderService.cancelOrder(orderId, userId));
    }

    // ================================
    // 📜 ORDER HISTORY
    // ================================
    @GetMapping("/history")
    public ResponseEntity<List<Order>> getOrderHistory(
            @RequestParam String userId) {

        return ResponseEntity.ok(orderService.getOrderHistory(userId));
    }

    // ================================
    // 🔍 GET SINGLE ORDER
    // ================================
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(
            @PathVariable String orderId,
            @RequestParam String userId) {

        return ResponseEntity.ok(orderService.getOrder(orderId, userId));
    }

    // ================================
    // 👨‍💼 ADMIN UPDATE STATUS
    // ================================
    @PutMapping("/status")
    public ResponseEntity<Order> updateStatus(
            @RequestParam String orderId,
            @RequestParam OrderStatus status) {

        return ResponseEntity.ok(orderService.updateStatus(orderId, status));
    }



    // buy now by clicking buy button without addint card
    @PostMapping("/buy-now")
    public ResponseEntity<Order> buyNow(
            @RequestParam String userId,
            @RequestParam String productId,
            @RequestParam int quantity,
            @RequestParam String address) {

        return ResponseEntity.ok(
                orderService.buyNow(userId, productId, quantity, address)
        );
    }
}