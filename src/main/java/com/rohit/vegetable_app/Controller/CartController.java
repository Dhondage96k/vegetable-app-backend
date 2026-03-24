package com.rohit.vegetable_app.Controller;

import com.rohit.vegetable_app.DTO.AddToCartRequest;
import com.rohit.vegetable_app.DTO.UpdateCartRequest;
import com.rohit.vegetable_app.Model.Card;
import com.rohit.vegetable_app.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public Card getCart(@RequestParam String userId) {
        return cartService.getCart(userId);
    }

    @PostMapping("/add")
    public ResponseEntity<Card> addToCart(@Valid @RequestBody AddToCartRequest request) {
        Card cart = cartService.addToCart(request);
        return ResponseEntity.ok(cart);
    }



    @PutMapping("/update")
    public ResponseEntity<Card> updateCart(@Valid @RequestBody UpdateCartRequest request) {
        return ResponseEntity.ok(cartService.updateQuantity(request));
    }



    @DeleteMapping("/remove/{userId}/{productId}")
    public ResponseEntity<Card> removeItem(
            @PathVariable String userId,
            @PathVariable String productId) {

        return ResponseEntity.ok(cartService.removeItem(userId, productId));
    }


    @DeleteMapping("/clear")
    public ResponseEntity<Card> clearCart(@RequestParam String userId) {
        return ResponseEntity.ok(cartService.clearCart(userId));
    }
}