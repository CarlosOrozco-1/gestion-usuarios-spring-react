package com.gestionusuarios.repository;

import com.gestionusuarios.dto.stats.ClientCredentialCount;
import com.gestionusuarios.entity.Client;
import com.gestionusuarios.entity.Credential;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CredentialRepositoryTest {

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private ClientRepository clientRepository;

    private Client clientA;
    private Client clientB;

    @BeforeEach
    void setUp() {
        clientA = clientRepository.save(Client.builder()
                .idNumber("CA1").nit("NITCA1").nombreRazonSocial("Acme Corp").regimenFiscal("GEN")
                .email("acme@test.com").active(true).build());
        clientB = clientRepository.save(Client.builder()
                .idNumber("CB1").nit("NITCB1").nombreRazonSocial("Beta Ltd").regimenFiscal("GEN")
                .email("beta@test.com").active(true).build());
    }

    private Credential buildCredential(Client client, String system) {
        return Credential.builder()
                .client(client)
                .systemName(system)
                .username("admin")
                .encryptedPassword("secret")
                .build();
    }

    @Test
    void findByClientId_ShouldReturnOnlyThatClientsCredentials() {
        credentialRepository.save(buildCredential(clientA, "SAP"));
        credentialRepository.save(buildCredential(clientA, "ERP"));
        credentialRepository.save(buildCredential(clientB, "CRM"));

        List<Credential> credentials = credentialRepository.findByClientId(clientA.getId());

        assertThat(credentials).hasSize(2);
        assertThat(credentials)
                .extracting(Credential::getSystemName)
                .containsExactlyInAnyOrder("SAP", "ERP");
    }

    @Test
    void countCredentialsByClient_ShouldGroupByClientName() {
        credentialRepository.save(buildCredential(clientA, "SAP"));
        credentialRepository.save(buildCredential(clientA, "ERP"));
        credentialRepository.save(buildCredential(clientB, "CRM"));

        List<ClientCredentialCount> counts = credentialRepository.countCredentialsByClient();

        assertThat(counts)
                .filteredOn(c -> c.clientName().equals("Acme Corp"))
                .singleElement()
                .extracting(ClientCredentialCount::count)
                .isEqualTo(2L);
        assertThat(counts)
                .filteredOn(c -> c.clientName().equals("Beta Ltd"))
                .singleElement()
                .extracting(ClientCredentialCount::count)
                .isEqualTo(1L);
    }
}
