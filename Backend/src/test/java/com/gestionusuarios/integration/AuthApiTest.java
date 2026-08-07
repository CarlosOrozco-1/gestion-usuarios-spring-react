package com.gestionusuarios.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthApiTest extends BaseApiTest {

    @Test
    void login_WithValidCredentials_ReturnsTokenAndUser() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("email", "admin@example.com", "password", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("admin@example.com"))
                .andExpect(jsonPath("$.user.roleName").value("ADMIN"));
    }

    @Test
    void login_WithWrongPassword_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("email", "admin@example.com", "password", "wrong-password"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_WithUnknownEmail_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("email", "ghost@example.com", "password", "password123"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_WithMalformedBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "not-an-email"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void protectedEndpoint_WithoutToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/system-users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_WithInvalidToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/system-users")
                        .header("Authorization", "Bearer invalid.token.value"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_WithAdminToken_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/system-users")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void endpoint_WithoutRequiredAuthority_ReturnsForbidden() throws Exception {
        int contadorRoleId = roleIdByName("CONTADOR");
        createUser("99999001", "contador@example.com", "password123", contadorRoleId);
        String token = login("contador@example.com", "password123");

        mockMvc.perform(get("/api/system-users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/clients")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
