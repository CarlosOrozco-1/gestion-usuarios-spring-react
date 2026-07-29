package com.gestionusuarios.repository;

import com.gestionusuarios.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Spring Data JPA derives the query from method name:
    // SELECT * FROM users WHERE email = ? AND active = true
    Optional<User> findByEmailAndActiveTrue(String email);

    boolean existsByIdNumber(String idNumber);
    boolean existsByEmail(String email);
}
