package com.gestionusuarios.repository;

import com.gestionusuarios.dto.stats.ClientCredentialCount;
import com.gestionusuarios.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CredentialRepository extends JpaRepository<Credential, Integer> {
    List<Credential> findByClientId(Integer clientId);

    @Query("select new com.gestionusuarios.dto.stats.ClientCredentialCount(c.businessName, count(cr)) " +
            "from Credential cr join cr.client c group by c.businessName order by count(cr) desc")
    List<ClientCredentialCount> countCredentialsByClient();
}