package com.gestionusuarios.repository;

import com.gestionusuarios.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    boolean existsByName(String name);
    Optional<Permission> findByName(String name);
}
