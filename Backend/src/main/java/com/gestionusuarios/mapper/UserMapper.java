package com.gestionusuarios.mapper;

import com.gestionusuarios.dto.response.UserResponse;
import com.gestionusuarios.entity.SystemUser;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(SystemUser user) {
        return UserResponse.builder()
                .id(user.getId())
                .idNumber(user.getIdNumber())
                .name(user.getName())
                .email(user.getEmail())
                .roleName(user.getRole().getName())
                .status(user.getActive() ? "ACTIVE" : "INACTIVE")
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}