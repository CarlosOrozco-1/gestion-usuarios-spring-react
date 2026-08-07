package com.gestionusuarios.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SystemUserApiTest extends BaseApiTest {

    private static final String USERS_URL = "/api/system-users";

    @Test
    void create_ReturnsCreatedUser() throws Exception {
        int roleId = roleIdByName("ADMIN");

        mockMvc.perform(post(USERS_URL)
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "idNumber", "1111",
                                "name", "Nuevo Usuario",
                                "email", "nuevo@example.com",
                                "password", "password123",
                                "roleId", roleId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idNumber").value("1111"))
                .andExpect(jsonPath("$.name").value("Nuevo Usuario"))
                .andExpect(jsonPath("$.email").value("nuevo@example.com"))
                .andExpect(jsonPath("$.roleName").value("ADMIN"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void create_WithDuplicateIdNumber_ReturnsConflict() throws Exception {
        int roleId = roleIdByName("ADMIN");
        createUser("2222", "first@example.com", "password123", roleId);

        mockMvc.perform(post(USERS_URL)
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "idNumber", "2222",
                                "name", "Otro",
                                "email", "other@example.com",
                                "password", "password123",
                                "roleId", roleId))))
                .andExpect(status().isConflict());
    }

    @Test
    void create_WithDuplicateEmail_ReturnsConflict() throws Exception {
        int roleId = roleIdByName("ADMIN");
        createUser("3333", "dup@example.com", "password123", roleId);

        mockMvc.perform(post(USERS_URL)
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "idNumber", "4444",
                                "name", "Duplicado",
                                "email", "dup@example.com",
                                "password", "password123",
                                "roleId", roleId))))
                .andExpect(status().isConflict());
    }

    @Test
    void create_WithMissingName_ReturnsBadRequest() throws Exception {
        String body = objectMapper.createObjectNode()
                .put("idNumber", "5555")
                .put("email", "novalid@example.com")
                .toString();

        mockMvc.perform(post(USERS_URL)
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").isNotEmpty());
    }

    @Test
    void findById_WithUnknownId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get(USERS_URL + "/99999")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_ReturnsUpdatedUser() throws Exception {
        int roleId = roleIdByName("ADMIN");
        int id = createUser("6666", "update@example.com", "password123", roleId);

        mockMvc.perform(put(USERS_URL + "/" + id)
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "idNumber", "6666",
                                "name", "Nombre Actualizado",
                                "email", "update@example.com",
                                "password", "newpass123",
                                "roleId", roleId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nombre Actualizado"));
    }

    @Test
    void deactivate_ThenLoginFails_ThenReactivate() throws Exception {
        int roleId = roleIdByName("CONTADOR");
        int id = createUser("7777", "toggle@example.com", "password123", roleId);

        mockMvc.perform(delete(USERS_URL + "/" + id)
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("email", "toggle@example.com", "password", "password123"))))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(patch(USERS_URL + "/" + id + "/reactivate")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("email", "toggle@example.com", "password", "password123"))))
                .andExpect(status().isOk());
    }
}
