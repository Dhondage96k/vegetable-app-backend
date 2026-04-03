package com.rohit.vegetable_app.Repository;

import com.rohit.vegetable_app.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email); // ✅ better than findByEmail for checks

}
