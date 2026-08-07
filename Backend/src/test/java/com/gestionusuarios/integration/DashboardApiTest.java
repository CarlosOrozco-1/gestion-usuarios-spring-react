package com.gestionusuarios.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DashboardApiTest extends BaseApiTest {

    private static final String STATS_URL = "/api/dashboard/stats";

    @Test
    void stats_ReturnsExpectedTotalsAndCharts() throws Exception {
        int clientA = createClient("D100", "statsa@example.com");
        createCredential(clientA, "SAP");
        createCredential(clientA, "ERP");
        createClient("D101", "statsb@example.com");
        int roleId = roleIdByName("CONTADOR");
        createUser("D102", "statsuser@example.com", "password123", roleId);

        mockMvc.perform(get(STATS_URL)
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(2))
                .andExpect(jsonPath("$.activeUsers").value(2))
                .andExpect(jsonPath("$.inactiveUsers").value(0))
                .andExpect(jsonPath("$.totalClients").value(2))
                .andExpect(jsonPath("$.activeClients").value(2))
                .andExpect(jsonPath("$.totalCredentials").value(2))
                .andExpect(jsonPath("$.totalRoles").value(3))
                .andExpect(jsonPath("$.totalPermissions").value(9))
                .andExpect(jsonPath("$.usersByRole").isArray())
                .andExpect(jsonPath("$.usersByRole[?(@.roleName == 'CONTADOR')].count").value(1))
                .andExpect(jsonPath("$.credentialsByClient").isArray())
                .andExpect(jsonPath("$.credentialsByClient[?(@.clientName == 'Cliente D100')].count").value(2))
                .andExpect(jsonPath("$.recentUsers").isArray())
                .andExpect(jsonPath("$.recentUsers.length()").value(2));
    }

    @Test
    void stats_WithoutToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get(STATS_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void stats_WithoutDashboardAuthority_ReturnsForbidden() throws Exception {
        String roleName = "SIN_PERMISOS_" + System.currentTimeMillis();
        String response = mockMvc.perform(post("/api/roles")
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", roleName,
                                "description", "Rol sin permisos"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        int roleId = objectMapper.readTree(response).get("id").asInt();

        createUser("D103", "noperm@example.com", "password123", roleId);
        String token = login("noperm@example.com", "password123");

        mockMvc.perform(get(STATS_URL)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
