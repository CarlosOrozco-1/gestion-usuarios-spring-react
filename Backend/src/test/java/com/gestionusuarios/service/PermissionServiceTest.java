package com.gestionusuarios.service;

import com.gestionusuarios.dto.request.PermissionRequest;
import com.gestionusuarios.dto.response.PermissionResponse;
import com.gestionusuarios.entity.Permission;
import com.gestionusuarios.exception.DuplicateResourceException;
import com.gestionusuarios.exception.ResourceNotFoundException;
import com.gestionusuarios.repository.PermissionRepository;
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
class PermissionServiceTest {

    @Mock private PermissionRepository permissionRepository;
    @InjectMocks private PermissionService permissionService;

    private Permission permission;

    @BeforeEach
    void setUp() {
        permission = Permission.builder()
                .id(1).name("CREATE_USER").description("Can create users")
                .resourcePath("/api/users").createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findAll_ShouldReturnPermissions() {
        when(permissionRepository.findAll()).thenReturn(List.of(permission));
        List<PermissionResponse> result = permissionService.findAll();
        assertEquals(1, result.size());
        assertEquals("CREATE_USER", result.get(0).getName());
    }

    @Test
    void findById_ShouldReturnPermission() {
        when(permissionRepository.findById(1)).thenReturn(Optional.of(permission));
        PermissionResponse result = permissionService.findById(1);
        assertEquals("CREATE_USER", result.getName());
    }

    @Test
    void findById_ShouldThrowWhenNotFound() {
        when(permissionRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> permissionService.findById(99));
    }

    @Test
    void create_ShouldSaveAndReturn() {
        PermissionRequest req = PermissionRequest.builder()
                .name("EDIT_USER").description("Edit users").resourcePath("/api/users").build();
        when(permissionRepository.existsByName("EDIT_USER")).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenAnswer(inv -> {
            Permission p = inv.getArgument(0);
            p.setId(2);
            return p;
        });

        PermissionResponse result = permissionService.create(req);
        assertEquals("EDIT_USER", result.getName());
    }

    @Test
    void create_ShouldThrowOnDuplicateName() {
        PermissionRequest req = PermissionRequest.builder().name("CREATE_USER").build();
        when(permissionRepository.existsByName("CREATE_USER")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> permissionService.create(req));
    }

    @Test
    void update_ShouldModifyPermission() {
        PermissionRequest req = PermissionRequest.builder()
                .name("CREATE_USER_UPDATED").description("Updated").resourcePath("/api/users").build();
        when(permissionRepository.findById(1)).thenReturn(Optional.of(permission));
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);

        PermissionResponse result = permissionService.update(1, req);
        assertEquals("CREATE_USER_UPDATED", result.getName());
    }

    @Test
    void update_ShouldThrowWhenNotFound() {
        PermissionRequest req = PermissionRequest.builder().name("X").build();
        when(permissionRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> permissionService.update(99, req));
    }

    @Test
    void delete_ShouldRemovePermission() {
        when(permissionRepository.existsById(1)).thenReturn(true);
        permissionService.delete(1);
        verify(permissionRepository).deleteById(1);
    }

    @Test
    void delete_ShouldThrowWhenNotFound() {
        when(permissionRepository.existsById(99)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> permissionService.delete(99));
    }
}
