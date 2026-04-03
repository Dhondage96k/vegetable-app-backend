package com.rohit.vegetable_app.service;

import com.rohit.vegetable_app.Enum.OrderStatus;
import com.rohit.vegetable_app.Enum.PaymentStatus;
import com.rohit.vegetable_app.Exception.BadRequestException;
import com.rohit.vegetable_app.Exception.ResourceNotFoundException;
import com.rohit.vegetable_app.Model.*;
import com.rohit.vegetable_app.Repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // ✅ PLACE ORDER
    @Transactional
    public Order placeOrder(String email, String address) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        // 🚨 Prevent duplicate order within 5 sec
        Order lastOrder = orderRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId());
        if (lastOrder != null &&
                Duration.between(lastOrder.getCreatedAt(), LocalDateTime.now()).getSeconds() < 5) {
            throw new BadRequestException("Duplicate order request");
        }

        List<OrderItem> items = cart.getItems().stream().map(ci -> {

            Product product = productRepository.findById(ci.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (product.getQuantity() < ci.getQuantity()) {
                throw new BadRequestException("Insufficient stock for " + product.getName());
            }

            product.setQuantity(product.getQuantity() - ci.getQuantity());
            productRepository.save(product);

            OrderItem oi = new OrderItem();
            oi.setProductId(ci.getProductId());
            oi.setProductName(ci.getProductName());
            oi.setPrice(ci.getPrice());
            oi.setQuantity(ci.getQuantity());

            return oi;
        }).toList();

        Order order = new Order();
        order.setUserId(user.getId());
        order.setItems(items);
        order.setTotalPrice(cart.getTotalPrice());
        order.setStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setDeliveryAddress(address);
        order.setCreatedAt(LocalDateTime.now());

        cart.getItems().clear();
        cart.setTotalPrice(0);
        cartRepository.save(cart);

        return orderRepository.save(order);
    }

    // ⚡ BUY NOW
    @Transactional
    public Order buyNow(String email, String productId, int quantity, String address) {

        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getQuantity() < quantity) {
            throw new BadRequestException("Insufficient stock");
        }

        // ✅ Reduce stock
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        OrderItem item = new OrderItem();
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setPrice(product.getPrice());
        item.setQuantity(quantity);

        Order order = new Order();
        order.setUserId(user.getId());
        order.setItems(List.of(item));
        order.setTotalPrice(product.getPrice() * quantity);
        order.setStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setDeliveryAddress(address);
        order.setCreatedAt(LocalDateTime.now());

        log.info("BuyNow order placed by {}", email);

        return orderRepository.save(order);
    }

    // ❌ CANCEL ORDER
    public Order cancelOrder(String orderId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getPaymentStatus() != PaymentStatus.UNPAID) {
            throw new BadRequestException("Paid orders cannot be cancelled");
        }

        long minutes = Duration.between(order.getCreatedAt(), LocalDateTime.now()).toMinutes();
        if (minutes > 30) {
            throw new BadRequestException("Cancellation window exceeded");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());

        log.info("Order {} cancelled by {}", orderId, email);

        return orderRepository.save(order);
    }

    // 📜 ORDER HISTORY
    public List<Order> getOrderHistory(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return orderRepository.findByUserId(user.getId())
                .stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .toList();
    }

    // 🔍 GET SINGLE ORDER
    public Order getOrder(String orderId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    // 👨‍💼 ADMIN UPDATE STATUS
    public Order updateStatus(String orderId, OrderStatus status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(status);

        if (status == OrderStatus.DELIVERED) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }

        log.info("Order {} updated to {}", orderId, status);

        return orderRepository.save(order);
    }
}