package com.gestionusuarios.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CredentialRequest {

    @NotBlank
    @Size(max = 150)
    private String systemName;

    @NotBlank
    @Size(max = 150)
    private String username;

    @NotBlank
    @Size(max = 500)
    private String encryptedPassword;

    @Size(max = 500)
    private String url;

    @Size(max = 1000)
    private String notes;

    private Integer clientId;
}