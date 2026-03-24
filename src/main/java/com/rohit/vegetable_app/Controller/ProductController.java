package com.rohit.vegetable_app.Controller;


import com.rohit.vegetable_app.Model.Product;
import com.rohit.vegetable_app.Repository.ProductRepository;
import com.rohit.vegetable_app.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /*@GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();

    }*/

    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable String id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return "Product deleted successfully";
    }


    //search
    @GetMapping
    public List<Product> getProducts(@RequestParam(required = false) String search) {
        return productService.searchProducts(search);
    }
}