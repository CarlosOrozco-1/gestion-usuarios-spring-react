package com.gestionusuarios.repository;

import com.gestionusuarios.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CredentialRepository extends JpaRepository<Credential, Integer> {
    List<Credential> findByClientId(Integer clientId);
}