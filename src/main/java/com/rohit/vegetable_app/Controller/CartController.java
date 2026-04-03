package com.rohit.vegetable_app.Controller;

import com.rohit.vegetable_app.DTO.AddToCartRequest;
import com.rohit.vegetable_app.DTO.UpdateCartRequest;
import com.rohit.vegetable_app.Model.Cart;
import com.rohit.vegetable_app.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
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

    // ✅ GET CART (JWT)
    @GetMapping
    public ResponseEntity<Cart> getCart(HttpServletRequest request) {

        String email = (String) request.getAttribute("email");

        return ResponseEntity.ok(cartService.getCart(email));
    }

    // ✅ ADD TO CART (JWT)
    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(
            HttpServletRequest request,
            @Valid @RequestBody AddToCartRequest req) {

        String email = (String) request.getAttribute("email");

        return ResponseEntity.ok(cartService.addToCart(email, req));
    }

    // ✅ UPDATE CART (JWT)
    @PutMapping("/update")
    public ResponseEntity<Cart> updateCart(
            HttpServletRequest request,
            @Valid @RequestBody UpdateCartRequest req) {

        String email = (String) request.getAttribute("email");

        return ResponseEntity.ok(cartService.updateQuantity(email, req));
    }

    // ✅ REMOVE ITEM (JWT)
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Cart> removeItem(
            HttpServletRequest request,
            @PathVariable String productId) {

        String email = (String) request.getAttribute("email");

        return ResponseEntity.ok(cartService.removeItem(email, productId));
    }

    // ✅ CLEAR CART (JWT)
    @DeleteMapping("/clear")
    public ResponseEntity<Cart> clearCart(HttpServletRequest request) {

        String email = (String) request.getAttribute("email");

        return ResponseEntity.ok(cartService.clearCart(email));
    }
}