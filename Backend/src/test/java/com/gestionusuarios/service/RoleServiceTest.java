package com.gestionusuarios.service;

import com.gestionusuarios.dto.request.RoleRequest;
import com.gestionusuarios.dto.response.RoleResponse;
import com.gestionusuarios.entity.Permission;
import com.gestionusuarios.entity.PermissionRole;
import com.gestionusuarios.entity.Role;
import com.gestionusuarios.entity.Status;
import com.gestionusuarios.exception.BadRequestException;
import com.gestionusuarios.exception.DuplicateResourceException;
import com.gestionusuarios.exception.ResourceNotFoundException;
import com.gestionusuarios.repository.PermissionRepository;
import com.gestionusuarios.repository.RoleRepository;
import com.gestionusuarios.repository.SystemUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock private RoleRepository roleRepository;
    @Mock private PermissionRepository permissionRepository;
    @Mock private SystemUserRepository systemUserRepository;
    @InjectMocks private RoleService roleService;

    private Role role;
    private Permission permission;

    @BeforeEach
    void setUp() {
        permission = Permission.builder()
                .id(1).name("CREATE_USER").description("Create users")
                .resourcePath("/api/users").createdAt(LocalDateTime.now())
                .build();

        role = Role.builder()
                .id(1).name("ADMIN").description("Administrator")
                .status(Status.ACTIVE).createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findAll_ShouldReturnRoles() {
        when(roleRepository.findAll()).thenReturn(List.of(role));
        List<RoleResponse> result = roleService.findAll();
        assertEquals(1, result.size());
        assertEquals("ADMIN", result.get(0).getName());
    }

    @Test
    void findById_ShouldReturnRole() {
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        RoleResponse result = roleService.findById(1);
        assertEquals("ADMIN", result.getName());
    }

    @Test
    void findById_ShouldThrowWhenNotFound() {
        when(roleRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> roleService.findById(99));
    }

    @Test
    void create_ShouldSaveAndReturn() {
        RoleRequest req = RoleRequest.builder().name("MANAGER").description("Manager").build();
        when(roleRepository.existsByName("MANAGER")).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenAnswer(inv -> {
            Role r = inv.getArgument(0);
            r.setId(2);
            return r;
        });

        RoleResponse result = roleService.create(req);
        assertEquals("MANAGER", result.getName());
    }

    @Test
    void create_ShouldThrowOnDuplicateName() {
        RoleRequest req = RoleRequest.builder().name("ADMIN").build();
        when(roleRepository.existsByName("ADMIN")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> roleService.create(req));
    }

    @Test
    void update_ShouldModifyRole() {
        RoleRequest req = RoleRequest.builder().name("ADMIN_UPDATED").description("Updated").build();
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleResponse result = roleService.update(1, req);
        assertEquals("ADMIN_UPDATED", result.getName());
    }

    @Test
    void delete_ShouldRemoveRole() {
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(systemUserRepository.existsByRoleId(1)).thenReturn(false);
        roleService.delete(1);
        verify(roleRepository).deleteById(1);
    }

    @Test
    void delete_ShouldThrowWhenNotFound() {
        when(roleRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> roleService.delete(99));
    }

    @Test
    void delete_ShouldThrowWhenRoleHasUsers() {
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(systemUserRepository.existsByRoleId(1)).thenReturn(true);
        assertThrows(BadRequestException.class, () -> roleService.delete(1));
    }

    @Test
    void assignPermissions_ShouldLinkPermissions() {
        List<Integer> permissionIds = List.of(1);
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(permissionRepository.findAllById(permissionIds)).thenReturn(List.of(permission));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleResponse result = roleService.assignPermissions(1, permissionIds);
        assertNotNull(result);
        verify(roleRepository).save(role);
    }

    @Test
    void assignPermissions_ShouldThrowWhenPermissionMissing() {
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(permissionRepository.findAllById(List.of(999))).thenReturn(List.of());
        assertThrows(BadRequestException.class, () -> roleService.assignPermissions(1, List.of(999)));
    }
}