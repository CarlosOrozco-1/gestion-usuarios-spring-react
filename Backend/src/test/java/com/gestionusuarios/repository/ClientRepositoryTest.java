package com.gestionusuarios.repository;

import com.gestionusuarios.entity.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    private Client buildClient(String id, String email, boolean active) {
        return Client.builder()
                .idNumber(id)
                .nit("NIT" + id)
                .businessName("Client " + id)
                .taxRegime("GEN")
                .email(email)
                .phone("555-0000")
                .address("Calle 1")
                .active(active)
                .build();
    }

    @Test
    void findByEmail_ShouldReturnClient() {
        clientRepository.save(buildClient("C01", "client@test.com", true));

        assertThat(clientRepository.findByEmail("client@test.com")).isPresent();
        assertThat(clientRepository.findByEmail("missing@test.com")).isEmpty();
    }

    @Test
    void existsBy_ShouldDetectIdNumberNitAndEmail() {
        clientRepository.save(buildClient("C02", "exists@test.com", true));

        assertThat(clientRepository.existsByIdNumber("C02")).isTrue();
        assertThat(clientRepository.existsByNit("NITC02")).isTrue();
        assertThat(clientRepository.existsByEmail("exists@test.com")).isTrue();
        assertThat(clientRepository.existsByIdNumber("ZZZ")).isFalse();
        assertThat(clientRepository.existsByNit("ZZZ")).isFalse();
    }

    @Test
    void countByActive_ShouldReturnPerStateCounts() {
        clientRepository.save(buildClient("C10", "a@test.com", true));
        clientRepository.save(buildClient("C11", "b@test.com", true));
        clientRepository.save(buildClient("C12", "c@test.com", false));

        assertThat(clientRepository.countByActive(true)).isEqualTo(2);
        assertThat(clientRepository.countByActive(false)).isEqualTo(1);
    }
}
