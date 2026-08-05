package com.gestionusuarios.mapper;

import com.gestionusuarios.dto.response.ClientResponse;
import com.gestionusuarios.entity.Client;

public final class ClientMapper {

    private ClientMapper() {
    }

    public static ClientResponse toResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .idNumber(client.getIdNumber())
                .name(client.getName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .address(client.getAddress())
                .active(client.getActive())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .build();
    }
}