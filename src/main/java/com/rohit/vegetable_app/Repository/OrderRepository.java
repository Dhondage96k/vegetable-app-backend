package com.rohit.vegetable_app.Repository;

import com.rohit.vegetable_app.Model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findByIdAndUserId(String id, String userId);
    List<Order> findByUserId(String userId);
}