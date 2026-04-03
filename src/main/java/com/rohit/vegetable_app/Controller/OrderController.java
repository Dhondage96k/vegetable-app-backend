package com.rohit.vegetable_app.Controller;

import com.rohit.vegetable_app.Enum.OrderStatus;
import com.rohit.vegetable_app.Model.Order;
import com.rohit.vegetable_app.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private String getEmail(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        if (email == null) {
            throw new RuntimeException("Unauthorized: Invalid JWT");
        }
        return email;
    }

    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(
            HttpServletRequest request,
            @RequestParam String address) {

        return ResponseEntity.ok(orderService.placeOrder(getEmail(request), address));
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<Order> cancelOrder(
            HttpServletRequest request,
            @RequestParam String orderId) {

        return ResponseEntity.ok(orderService.cancelOrder(orderId, getEmail(request)));
    }

    @GetMapping("/history")
    public ResponseEntity<List<Order>> history(HttpServletRequest request) {
        return ResponseEntity.ok(orderService.getOrderHistory(getEmail(request)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(
            HttpServletRequest request,
            @PathVariable String orderId) {

        return ResponseEntity.ok(orderService.getOrder(orderId, getEmail(request)));
    }

    @PutMapping("/status")
    public ResponseEntity<Order> updateStatus(
            @RequestParam String orderId,
            @RequestParam OrderStatus status) {

        return ResponseEntity.ok(orderService.updateStatus(orderId, status));
    }

    @PostMapping("/buy-now")
    public ResponseEntity<Order> buyNow(
            HttpServletRequest request,
            @RequestParam String productId,
            @RequestParam int quantity,
            @RequestParam String address) {

        return ResponseEntity.ok(
                orderService.buyNow(getEmail(request), productId, quantity, address)
        );
    }
}