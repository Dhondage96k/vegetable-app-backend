package com.rohit.vegetable_app.service;

import com.rohit.vegetable_app.Model.Category;
import com.rohit.vegetable_app.Model.Product;
import com.rohit.vegetable_app.Repository.CategoryRepository;
import com.rohit.vegetable_app.Repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
   private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product addProduct(Product product, String categorySlug) {

        Category category = categoryRepository.findBySlug(categorySlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        product.setCategoryId(category.getId());
        product.setCategoryName(category.getName()); // if you added this field

        return productRepository.save(product);
    }
    public Product updateProduct(String id, Product updatedProduct, String categorySlug) {

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        Category category = categoryRepository.findBySlug(categorySlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setQuantity(updatedProduct.getQuantity());

        existing.setCategoryId(category.getId());
        existing.setCategoryName(category.getName());

        return productRepository.save(existing);
    }

    //delete product
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        productRepository.delete(product);
    }


    //search
    public List<Product> searchProducts(String search) {

        if (search == null || search.trim().isEmpty()) {
            return productRepository.findAll(); // fallback
        }

        return productRepository.findByNameContainingIgnoreCase(search);
    }

    public List<Product> getProductsByCategory(String slug) {

        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        return productRepository.findByCategoryId(category.getId());
    }
}