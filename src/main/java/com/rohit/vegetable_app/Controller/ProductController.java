package com.rohit.vegetable_app.Controller;

import com.rohit.vegetable_app.Model.Product;
import com.rohit.vegetable_app.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ✅ GET ALL PRODUCTS
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // ✅ GET PRODUCTS BY CATEGORY
    @GetMapping("/category/{slug}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getProductsByCategory(slug));
    }

    // ✅ SEARCH PRODUCTS
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(productService.searchProducts(query));
    }

    // ✅ ADD PRODUCT
    @PostMapping("/{categorySlug}")
    public ResponseEntity<Product> addProduct(
            @PathVariable String categorySlug,
            @Valid @RequestBody Product product) {

        return ResponseEntity.ok(productService.addProduct(product, categorySlug));
    }

    // ✅ UPDATE PRODUCT
    @PutMapping("/{id}/{categorySlug}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String id,
            @PathVariable String categorySlug,
            @Valid @RequestBody Product product) {

        return ResponseEntity.ok(productService.updateProduct(id, product, categorySlug));
    }

    // ✅ DELETE PRODUCT
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}