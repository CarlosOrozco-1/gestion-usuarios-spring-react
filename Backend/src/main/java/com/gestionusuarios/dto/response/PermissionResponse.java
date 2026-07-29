package com.gestionusuarios.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponse {
    private Integer id;
    private String name;
    private String description;
    private String resourcePath;
    private LocalDateTime createdAt;
}
