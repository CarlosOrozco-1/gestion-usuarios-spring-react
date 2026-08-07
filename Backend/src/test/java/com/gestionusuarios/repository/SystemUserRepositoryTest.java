package com.gestionusuarios.repository;

import com.gestionusuarios.dto.stats.RoleUserCount;
import com.gestionusuarios.entity.Role;
import com.gestionusuarios.entity.Status;
import com.gestionusuarios.entity.SystemUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SystemUserRepositoryTest {

    @Autowired
    private SystemUserRepository systemUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role adminRole;
    private Role viewerRole;

    @BeforeEach
    void setUp() {
        adminRole = roleRepository.save(Role.builder().name("ADMIN").description("Admin role").status(Status.ACTIVE).build());
        viewerRole = roleRepository.save(Role.builder().name("VIEWER").description("Viewer role").status(Status.ACTIVE).build());
    }

    private SystemUser buildUser(String id, String email, Role role, boolean active) {
        return SystemUser.builder()
                .idNumber(id)
                .name("User " + id)
                .email(email)
                .password("encoded")
                .role(role)
                .active(active)
                .build();
    }

    @Test
    void findByEmailAndActiveTrue_ShouldReturnOnlyActiveUser() {
        systemUserRepository.save(buildUser("001", "active@test.com", adminRole, true));
        systemUserRepository.save(buildUser("002", "inactive@test.com", adminRole, false));

        Optional<SystemUser> found = systemUserRepository.findByEmailAndActiveTrue("active@test.com");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("active@test.com");

        assertThat(systemUserRepository.findByEmailAndActiveTrue("inactive@test.com")).isEmpty();
    }

    @Test
    void existsBy_ShouldDetectIdNumberAndEmail() {
        systemUserRepository.save(buildUser("100", "exists@test.com", adminRole, true));

        assertThat(systemUserRepository.existsByIdNumber("100")).isTrue();
        assertThat(systemUserRepository.existsByIdNumber("999")).isFalse();
        assertThat(systemUserRepository.existsByEmail("exists@test.com")).isTrue();
        assertThat(systemUserRepository.existsByEmail("nope@test.com")).isFalse();
    }

    @Test
    void countByActive_ShouldReturnPerStateCounts() {
        systemUserRepository.save(buildUser("010", "a@test.com", adminRole, true));
        systemUserRepository.save(buildUser("011", "b@test.com", adminRole, true));
        systemUserRepository.save(buildUser("012", "c@test.com", adminRole, false));

        assertThat(systemUserRepository.countByActive(true)).isEqualTo(2);
        assertThat(systemUserRepository.countByActive(false)).isEqualTo(1);
    }

    @Test
    void countUsersByRole_ShouldGroupByRoleName() {
        systemUserRepository.save(buildUser("020", "u1@test.com", adminRole, true));
        systemUserRepository.save(buildUser("021", "u2@test.com", adminRole, true));
        systemUserRepository.save(buildUser("022", "u3@test.com", viewerRole, true));

        List<RoleUserCount> counts = systemUserRepository.countUsersByRole();

        assertThat(counts)
                .extracting(RoleUserCount::roleName)
                .containsExactlyInAnyOrder("ADMIN", "VIEWER");
        assertThat(counts)
                .filteredOn(c -> c.roleName().equals("ADMIN"))
                .singleElement()
                .extracting(RoleUserCount::count)
                .isEqualTo(2L);
        assertThat(counts)
                .filteredOn(c -> c.roleName().equals("VIEWER"))
                .singleElement()
                .extracting(RoleUserCount::count)
                .isEqualTo(1L);
    }

    @Test
    void findTop5ByOrderByCreatedAtDesc_ShouldReturnAtMostFiveMostRecent() throws InterruptedException {
        for (int i = 1; i <= 6; i++) {
            systemUserRepository.save(buildUser("0" + i, "recent" + i + "@test.com", adminRole, true));
            systemUserRepository.flush();
            Thread.sleep(5);
        }

        List<SystemUser> recent = systemUserRepository.findTop5ByOrderByCreatedAtDesc();

        assertThat(recent).hasSize(5);
        assertThat(recent.get(0).getEmail()).isEqualTo("recent6@test.com");
        assertThat(recent)
                .extracting(SystemUser::getEmail)
                .doesNotContain("recent1@test.com");
    }
}
