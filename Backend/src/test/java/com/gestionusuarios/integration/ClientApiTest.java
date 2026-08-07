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
                                "nit", "100200300",
                                "businessName", "Cliente Demo",
                                "taxRegime", "GEN",
                                "birthDate", "1990-05-15",
                                "email", "demo@example.com",
                                "phone", "555-1111",
                                "address", "Av. Principal 123"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.businessName").value("Cliente Demo"))
                .andExpect(jsonPath("$.nit").value("100200300"))
                .andExpect(jsonPath("$.taxRegime").value("GEN"))
                .andExpect(jsonPath("$.birthDate").value("1990-05-15"))
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
                                "nit", "200300400",
                                "businessName", "Otro",
                                "taxRegime", "GEN",
                                "email", "clientdup@example.com"))))
                .andExpect(status().isConflict());
    }

    @Test
    void create_WithDuplicateNit_ReturnsConflict() throws Exception {
        createClient("C103", "NITDUP", "nitdup@example.com");

        mockMvc.perform(post(CLIENTS_URL)
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "idNumber", "C104",
                                "nit", "NITDUP",
                                "businessName", "Otro",
                                "taxRegime", "GEN",
                                "email", "other@example.com"))))
                .andExpect(status().isConflict());
    }

    @Test
    void create_WithMissingEmail_ReturnsBadRequest() throws Exception {
        String body = objectMapper.createObjectNode()
                .put("idNumber", "C105")
                .put("nit", "300400500")
                .put("businessName", "Sin Correo")
                .put("taxRegime", "GEN")
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
        int id = createClient("C106", "clientupd@example.com");

        mockMvc.perform(put(CLIENTS_URL + "/" + id)
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "idNumber", "C106",
                                "nit", "400500600",
                                "businessName", "Cliente Actualizado",
                                "taxRegime", "PEQ",
                                "birthDate", "1988-11-22",
                                "email", "clientupd@example.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.businessName").value("Cliente Actualizado"))
                .andExpect(jsonPath("$.nit").value("400500600"))
                .andExpect(jsonPath("$.taxRegime").value("PEQ"))
                .andExpect(jsonPath("$.birthDate").value("1988-11-22"));
    }

    @Test
    void deactivate_ThenReactivate() throws Exception {
        int id = createClient("C107", "clienttoggle@example.com");

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
