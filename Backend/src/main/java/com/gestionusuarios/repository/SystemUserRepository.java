package com.gestionusuarios.repository;

import com.gestionusuarios.entity.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemUserRepository extends JpaRepository<SystemUser, Integer> {
    Optional<SystemUser> findByEmailAndActiveTrue(String email);
    boolean existsByIdNumber(String idNumber);
    boolean existsByEmail(String email);
    boolean existsByRoleId(Integer roleId);
}