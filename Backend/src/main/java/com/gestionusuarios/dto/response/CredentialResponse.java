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
public class CredentialResponse {
    private Integer id;
    private Integer clientId;
    private String clientName;
    private String systemName;
    private String username;
    private String encryptedPassword;
    private String url;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}