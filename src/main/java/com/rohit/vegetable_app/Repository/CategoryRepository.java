package com.rohit.vegetable_app.Repository;

import com.rohit.vegetable_app.Model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {

    Optional<Category> findBySlug(String slug);
}