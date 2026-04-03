package com.rohit.vegetable_app.service;

import com.rohit.vegetable_app.DTO.AddToCartRequest;
import com.rohit.vegetable_app.DTO.UpdateCartRequest;
import com.rohit.vegetable_app.Exception.BadRequestException;
import com.rohit.vegetable_app.Exception.ResourceNotFoundException;
import com.rohit.vegetable_app.Model.Cart;
import com.rohit.vegetable_app.Model.CartItem;
import com.rohit.vegetable_app.Model.Product;
import com.rohit.vegetable_app.Model.User;
import com.rohit.vegetable_app.Repository.CartRepository;
import com.rohit.vegetable_app.Repository.ProductRepository;
import com.rohit.vegetable_app.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // ✅ GET CART
    public Cart getCart(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> createNewCart(user.getId()));
    }

    // ✅ CREATE CART
    private Cart createNewCart(String userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>());
        cart.setTotalPrice(0);
        return cartRepository.save(cart);
    }

    // ✅ ADD TO CART
    public Cart addToCart(String email, AddToCartRequest request) {

        if (request.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> createNewCart(user.getId()));

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // ❗ STOCK CHECK
        if (product.getQuantity() < request.getQuantity()) {
            throw new BadRequestException("Not enough stock available");
        }

        Optional<CartItem> optionalItem = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(product.getId()))
                .findFirst();

        if (optionalItem.isPresent()) {
            CartItem item = optionalItem.get();
            int newQty = item.getQuantity() + request.getQuantity();

            if (product.getQuantity() < newQty) {
                throw new BadRequestException("Exceeds available stock");
            }

            item.setQuantity(newQty);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProductId(product.getId());
            newItem.setProductName(product.getName());
            newItem.setPrice(product.getPrice());
            newItem.setQuantity(request.getQuantity());

            cart.getItems().add(newItem);
        }

        recalculateTotal(cart);

        return cartRepository.save(cart);
    }

    // ✅ UPDATE QUANTITY
    public Cart updateQuantity(String email, UpdateCartRequest request) {

        if (request.getQuantity() < 0) {
            throw new BadRequestException("Quantity cannot be negative");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        if (request.getQuantity() == 0) {
            cart.getItems().remove(item);
        } else {

            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (product.getQuantity() < request.getQuantity()) {
                throw new BadRequestException("Not enough stock");
            }

            item.setQuantity(request.getQuantity());
        }

        recalculateTotal(cart);

        return cartRepository.save(cart);
    }

    // ✅ REMOVE ITEM
    public Cart removeItem(String email, String productId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        if (!removed) {
            throw new ResourceNotFoundException("Item not found in cart");
        }

        recalculateTotal(cart);

        return cartRepository.save(cart);
    }

    // ✅ CLEAR CART
    public Cart clearCart(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.setItems(new ArrayList<>());
        cart.setTotalPrice(0);

        return cartRepository.save(cart);
    }

    // ✅ COMMON METHOD (IMPORTANT)
    private void recalculateTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        cart.setTotalPrice(total);
    }
}