package com.rohit.vegetable_app.service;

import com.rohit.vegetable_app.Enum.OrderStatus;
import com.rohit.vegetable_app.Enum.PaymentStatus;
import com.rohit.vegetable_app.Exception.BadRequestException;
import com.rohit.vegetable_app.Exception.ResourceNotFoundException;
import com.rohit.vegetable_app.Model.*;
import com.rohit.vegetable_app.Repository.CartRepository;
import com.rohit.vegetable_app.Repository.OrderRepository;
import com.rohit.vegetable_app.Repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository , ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    // ================================
    // ✅ PLACE ORDER (REAL WORLD)
    // ================================
    public Order placeOrder(String userId, String address) {

        Card cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        // Convert CartItem -> OrderItem
        List<OrderItem> orderItems = cart.getItems().stream().map(item -> {
            OrderItem oi = new OrderItem();
            oi.setProductId(item.getProductId());
            oi.setProductName(item.getProductName());
            oi.setPrice(item.getPrice());
            oi.setQuantity(item.getQuantity());
            return oi;
        }).toList();

        Order order = new Order();
        order.setUserId(userId);
        order.setItems(orderItems);
        order.setTotalPrice(cart.getTotalPrice());
        order.setStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setDeliveryAddress(address);
        order.setCreatedAt(LocalDateTime.now());

        // ✅ Clear cart after order
        cart.getItems().clear();
        cart.setTotalPrice(0);
        cartRepository.save(cart);

        return orderRepository.save(order);
    }

    // ================================
    // ❌ CANCEL ORDER
    // ================================
    public Order cancelOrder(String orderId, String userId) {

        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Only unpaid orders
        if (order.getPaymentStatus() != PaymentStatus.UNPAID) {
            throw new BadRequestException("Paid orders cannot be cancelled");
        }

        // Only within 30 minutes
        long minutes = Duration.between(order.getCreatedAt(), LocalDateTime.now()).toMinutes();
        if (minutes > 30) {
            throw new BadRequestException("Cancellation window exceeded");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    // ================================
    // 📜 ORDER HISTORY
    // ================================
    public List<Order> getOrderHistory(String userId) {

        return orderRepository.findByUserId(userId)
                .stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .toList();
    }

    // ================================
    // 🔍 GET SINGLE ORDER
    // ================================
    public Order getOrder(String orderId, String userId) {

        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    // ================================
    // 👨‍💼 ADMIN UPDATE STATUS
    // ================================
    public Order updateStatus(String orderId, OrderStatus status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(status);

        // Auto update payment when delivered (optional logic)
        if (status == OrderStatus.DELIVERED) {
            order.setPaymentStatus(PaymentStatus.PAID.PAID);
        }

        return orderRepository.save(order);
    }

ProductRepository productRepository;
    public Order buyNow(String userId, String productId, int quantity, String address) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        OrderItem item = new OrderItem();
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setPrice(product.getPrice());
        item.setQuantity(quantity);

        Order order = new Order();
        order.setUserId(userId);
        order.setItems(List.of(item));
        order.setTotalPrice(product.getPrice() * quantity);
        order.setStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setDeliveryAddress(address);
        order.setCreatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }
}
