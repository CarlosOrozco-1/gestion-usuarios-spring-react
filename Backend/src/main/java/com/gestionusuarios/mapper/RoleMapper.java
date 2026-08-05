package com.gestionusuarios.mapper;

import com.gestionusuarios.dto.response.PermissionResponse;
import com.gestionusuarios.dto.response.RoleResponse;
import com.gestionusuarios.entity.Role;

import java.util.List;

public final class RoleMapper {

    private RoleMapper() {
    }

    public static RoleResponse toResponse(Role role) {
        List<PermissionResponse> permissions = role.getRolePermissions().stream()
                .map(pr -> PermissionMapper.toResponse(pr.getPermission()))
                .toList();

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .status(role.getStatus().name())
                .createdAt(role.getCreatedAt())
                .permissions(permissions)
                .build();
    }
}
