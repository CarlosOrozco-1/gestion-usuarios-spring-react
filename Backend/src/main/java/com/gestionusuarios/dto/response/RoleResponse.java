package com.gestionusuarios.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private Integer id;
    private String name;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private List<PermissionResponse> permissions;
}
