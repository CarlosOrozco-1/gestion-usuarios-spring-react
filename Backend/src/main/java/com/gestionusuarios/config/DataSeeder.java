package com.gestionusuarios.config;

import com.gestionusuarios.entity.Permission;
import com.gestionusuarios.entity.PermissionRole;
import com.gestionusuarios.entity.Role;
import com.gestionusuarios.entity.Status;
import com.gestionusuarios.entity.SystemUser;
import com.gestionusuarios.repository.PermissionRepository;
import com.gestionusuarios.repository.RoleRepository;
import com.gestionusuarios.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final SystemUserRepository systemUserRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Map<String, String> PERMISSIONS = new LinkedHashMap<>();

    static {
        // System User permissions
        PERMISSIONS.put("ADMIN_USERS", "/api/system-users");
        PERMISSIONS.put("ADMIN_ROLES", "/api/roles");
        PERMISSIONS.put("ADMIN_PERMISSIONS", "/api/permissions");
        PERMISSIONS.put("ASSIGN_PERMISSIONS", "/api/roles/{id}/permissions");

        // Client permissions
        PERMISSIONS.put("VIEW_CLIENTS", "/api/clients");
        PERMISSIONS.put("MANAGE_CLIENTS", "/api/clients");

        // Credential permissions
        PERMISSIONS.put("VIEW_CREDENTIALS", "/api/credentials");
        PERMISSIONS.put("MANAGE_CREDENTIALS", "/api/credentials");
    }

    private static final Map<String, Set<String>> ROLE_PERMISSIONS = new LinkedHashMap<>();

    static {
        ROLE_PERMISSIONS.put("ADMIN", Set.of(
                "ADMIN_USERS", "ADMIN_ROLES", "ADMIN_PERMISSIONS", "ASSIGN_PERMISSIONS",
                "VIEW_CLIENTS", "MANAGE_CLIENTS",
                "VIEW_CREDENTIALS", "MANAGE_CREDENTIALS"
        ));
        ROLE_PERMISSIONS.put("CONTADOR", Set.of(
                "VIEW_CLIENTS", "MANAGE_CLIENTS",
                "VIEW_CREDENTIALS", "MANAGE_CREDENTIALS"
        ));
        ROLE_PERMISSIONS.put("ASISTENTE", Set.of(
                "VIEW_CLIENTS",
                "VIEW_CREDENTIALS"
        ));
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedPermissions();
        seedRoles();
        seedAdminUser();
    }

    private void seedPermissions() {
        for (Map.Entry<String, String> entry : PERMISSIONS.entrySet()) {
            if (!permissionRepository.existsByName(entry.getKey())) {
                permissionRepository.save(Permission.builder()
                        .name(entry.getKey())
                        .description(entry.getKey().replace("_", " ").toLowerCase())
                        .resourcePath(entry.getValue())
                        .build());
            }
        }
    }

    private void seedRoles() {
        for (Map.Entry<String, Set<String>> entry : ROLE_PERMISSIONS.entrySet()) {
            String roleName = entry.getKey();
            Set<String> permNames = entry.getValue();

            if (roleRepository.findByName(roleName).isPresent()) {
                continue;
            }

            Role role = roleRepository.save(Role.builder()
                    .name(roleName)
                    .description(roleName + " role")
                    .status(Status.ACTIVE)
                    .build());

            for (String permName : permNames) {
                permissionRepository.findByName(permName).ifPresent(permission -> {
                    role.getRolePermissions().add(PermissionRole.builder()
                            .role(role)
                            .permission(permission)
                            .build());
                });
            }

            roleRepository.save(role);
        }
    }

    private void seedAdminUser() {
        if (systemUserRepository.existsByEmail("admin@example.com")) {
            return;
        }

        Role admin = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role must exist before seeding the admin user"));

        systemUserRepository.save(SystemUser.builder()
                .idNumber("0000000000")
                .name("Administrator")
                .email("admin@example.com")
                .password(passwordEncoder.encode("password123"))
                .role(admin)
                .active(true)
                .build());
    }
}