package com.gestionusuarios.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CredentialApiTest extends BaseApiTest {

    private static final String CREDENTIALS_URL = "/api/credentials";

    @Test
    void create_ReturnsCreatedCredential() throws Exception {
        int clientId = createClient("K100", "credclient@example.com");

        mockMvc.perform(post(CREDENTIALS_URL)
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "clientId", clientId,
                                "systemName", "SAP",
                                "username", "jdoe",
                                "encryptedPassword", "secret123",
                                "url", "https://sap.example.com"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientName").value("Cliente K100"))
                .andExpect(jsonPath("$.systemName").value("SAP"))
                .andExpect(jsonPath("$.username").value("jdoe"));
    }

    @Test
    void create_WithUnknownClient_ReturnsNotFound() throws Exception {
        mockMvc.perform(post(CREDENTIALS_URL)
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "clientId", 999999,
                                "systemName", "ERP",
                                "username", "user",
                                "encryptedPassword", "secret"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_WithMissingFields_ReturnsBadRequest() throws Exception {
        int clientId = createClient("K101", "badcred@example.com");

        String body = objectMapper.createObjectNode()
                .put("clientId", clientId)
                .put("systemName", "ERP")
                .toString();

        mockMvc.perform(post(CREDENTIALS_URL)
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findByClientId_ReturnsOnlyThatClientsCredentials() throws Exception {
        int clientA = createClient("K102", "a@example.com");
        int clientB = createClient("K103", "b@example.com");
        createCredential(clientA, "SAP");
        createCredential(clientA, "ERP");
        createCredential(clientB, "CRM");

        mockMvc.perform(get(CREDENTIALS_URL + "/client/" + clientA)
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.systemName == 'SAP')]").exists())
                .andExpect(jsonPath("$[?(@.systemName == 'ERP')]").exists());
    }

    @Test
    void delete_ReturnsNoContent_ThenNotFound() throws Exception {
        int clientId = createClient("K104", "delcred@example.com");
        int credentialId = createCredential(clientId, "SAP");

        mockMvc.perform(delete(CREDENTIALS_URL + "/" + credentialId)
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(CREDENTIALS_URL + "/" + credentialId)
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isNotFound());
    }
}
