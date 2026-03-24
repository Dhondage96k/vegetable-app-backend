package com.rohit.vegetable_app.Repository;

import com.rohit.vegetable_app.Model.Card;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<Card, String> {
    Optional<Card> findByUserId(String userId);
}