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

class ClientApiTest extends BaseApiTest {

    private static final String CLIENTS_URL = "/api/clients";

    @Test
    void create_ReturnsCreatedClient() throws Exception {
        mockMvc.perform(post(CLIENTS_URL)
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "idNumber", "C100",
                                "name", "Cliente Demo",
                                "email", "demo@example.com",
                                "phone", "555-1111",
                                "address", "Av. Principal 123"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cliente Demo"))
                .andExpect(jsonPath("$.email").value("demo@example.com"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void create_WithDuplicateEmail_ReturnsConflict() throws Exception {
        createClient("C101", "clientdup@example.com");

        mockMvc.perform(post(CLIENTS_URL)
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "idNumber", "C102",
                                "name", "Otro",
                                "email", "clientdup@example.com"))))
                .andExpect(status().isConflict());
    }

    @Test
    void create_WithMissingEmail_ReturnsBadRequest() throws Exception {
        String body = objectMapper.createObjectNode()
                .put("idNumber", "C103")
                .put("name", "Sin Correo")
                .toString();

        mockMvc.perform(post(CLIENTS_URL)
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findById_WithUnknownId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get(CLIENTS_URL + "/99999")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_ReturnsUpdatedClient() throws Exception {
        int id = createClient("C104", "clientupd@example.com");

        mockMvc.perform(put(CLIENTS_URL + "/" + id)
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "idNumber", "C104",
                                "name", "Cliente Actualizado",
                                "email", "clientupd@example.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cliente Actualizado"));
    }

    @Test
    void deactivate_ThenReactivate() throws Exception {
        int id = createClient("C105", "clienttoggle@example.com");

        mockMvc.perform(delete(CLIENTS_URL + "/" + id)
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(CLIENTS_URL)
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")].active").value(false));

        mockMvc.perform(patch(CLIENTS_URL + "/" + id + "/reactivate")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk());
    }
}
