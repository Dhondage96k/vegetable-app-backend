package com.rohit.vegetable_app.service;

import com.rohit.vegetable_app.DTO.AddToCartRequest;
import com.rohit.vegetable_app.DTO.UpdateCartRequest;
import com.rohit.vegetable_app.Exception.ResourceNotFoundException;
import com.rohit.vegetable_app.Model.Card;
import com.rohit.vegetable_app.Model.CartItem;
import com.rohit.vegetable_app.Model.Product;
import com.rohit.vegetable_app.Repository.CartRepository;
import com.rohit.vegetable_app.Repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;  // ✅ add this

    // ✅ inject BOTH repositories
    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }
    public Card getCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));
    }

    private Card createNewCart(String userId) {
        Card cart = new Card();
        cart.setUserId(userId);
        return cartRepository.save(cart);
    }






    // add to card
    public Card addToCart(AddToCartRequest request) {

        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Card cart = cartRepository.findByUserId(request.getUserId())
                .orElseGet(() -> createNewCart(request.getUserId()));
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"+ request.getProductId()));

        // Use Map for fast lookup (O(1))
        Map<String, CartItem> itemMap = cart.getItems().stream()
                .collect(Collectors.toMap(CartItem::getProductId, item -> item));

        CartItem item = itemMap.get(product.getId());

        if (item != null) {
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setProductId(product.getId());
            newItem.setProductName(product.getName());
            newItem.setPrice(product.getPrice());
            newItem.setQuantity(request.getQuantity());

            cart.getItems().add(newItem);
        }

        // Recalculate total (clean way)
        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        cart.setTotalPrice(total);

        return cartRepository.save(cart);
    }



    // update card
    public Card updateQuantity(UpdateCartRequest request) {

        Card cart = cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        Optional<CartItem> optionalItem = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst();

        if (request.getQuantity() == 0) {
            // remove safely
            optionalItem.ifPresent(cart.getItems()::remove);
        } else {
            CartItem item = optionalItem
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

            item.setQuantity(request.getQuantity());
        }

        // Recalculate total safely
        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        cart.setTotalPrice(total);

        return cartRepository.save(cart);
    }



    // delete
    public Card removeItem(String userId, String productId) {

        Card cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems() != null) {
            cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        }

        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        cart.setTotalPrice(total);

        return cartRepository.save(cart);
    }



    //clean
    public Card clearCart(String userId) {

        Card cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getItems().clear();
        cart.setTotalPrice(0);

        return cartRepository.save(cart);
    }
}