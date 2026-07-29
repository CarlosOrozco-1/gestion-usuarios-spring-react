package com.gestionusuarios.repository;

import com.gestionusuarios.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    boolean existsByName(String name);
}
