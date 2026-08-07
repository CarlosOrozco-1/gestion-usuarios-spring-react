package com.gestionusuarios.mapper;

import com.gestionusuarios.dto.response.CredentialResponse;
import com.gestionusuarios.entity.Credential;

public final class CredentialMapper {

    private CredentialMapper() {
    }

    public static CredentialResponse toResponse(Credential credential) {
        return CredentialResponse.builder()
                .id(credential.getId())
                .clientId(credential.getClient().getId())
                .clientName(credential.getClient().getNombreRazonSocial())
                .systemName(credential.getSystemName())
                .username(credential.getUsername())
                .encryptedPassword(credential.getEncryptedPassword())
                .url(credential.getUrl())
                .notes(credential.getNotes())
                .createdAt(credential.getCreatedAt())
                .updatedAt(credential.getUpdatedAt())
                .build();
    }
}