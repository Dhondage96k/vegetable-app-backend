package com.rohit.vegetable_app.Repository;

import com.rohit.vegetable_app.Model.Category;
import com.rohit.vegetable_app.Model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryId(String categoryId);
}
