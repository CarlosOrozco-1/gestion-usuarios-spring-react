package com.gestionusuarios.mapper;

import com.gestionusuarios.dto.response.PermissionResponse;
import com.gestionusuarios.entity.Permission;

public final class PermissionMapper {

    private PermissionMapper() {
    }

    public static PermissionResponse toResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .resourcePath(permission.getResourcePath())
                .createdAt(permission.getCreatedAt())
                .build();
    }
}
