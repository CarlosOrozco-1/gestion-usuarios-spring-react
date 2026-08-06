package com.gestionusuarios.repository;

import com.gestionusuarios.dto.stats.RoleUserCount;
import com.gestionusuarios.entity.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SystemUserRepository extends JpaRepository<SystemUser, Integer> {
    Optional<SystemUser> findByEmailAndActiveTrue(String email);
    boolean existsByIdNumber(String idNumber);
    boolean existsByEmail(String email);
    boolean existsByRoleId(Integer roleId);

    long countByActive(boolean active);

    @Query("select new com.gestionusuarios.dto.stats.RoleUserCount(r.name, count(u)) " +
            "from SystemUser u join u.role r group by r.name order by count(u) desc")
    List<RoleUserCount> countUsersByRole();

    List<SystemUser> findTop5ByOrderByCreatedAtDesc();
}