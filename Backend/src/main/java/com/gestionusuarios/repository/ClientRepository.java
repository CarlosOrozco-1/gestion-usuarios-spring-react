package com.gestionusuarios.repository;

import com.gestionusuarios.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    Optional<Client> findByEmail(String email);
    boolean existsByIdNumber(String idNumber);
    boolean existsByEmail(String email);
}