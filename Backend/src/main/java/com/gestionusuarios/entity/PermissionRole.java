package com.gestionusuarios.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permission_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

}
