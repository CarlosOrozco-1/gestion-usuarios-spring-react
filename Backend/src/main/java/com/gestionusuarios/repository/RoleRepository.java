package com.gestionusuarios.repository;

import com.gestionusuarios.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    boolean existsByName(String name);
}
