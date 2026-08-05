package com.gestionusuarios.service;

import com.gestionusuarios.dto.request.UserRequest;
import com.gestionusuarios.dto.response.UserResponse;
import com.gestionusuarios.entity.Role;
import com.gestionusuarios.entity.Status;
import com.gestionusuarios.entity.SystemUser;
import com.gestionusuarios.exception.BadRequestException;
import com.gestionusuarios.exception.DuplicateResourceException;
import com.gestionusuarios.exception.ResourceNotFoundException;
import com.gestionusuarios.repository.RoleRepository;
import com.gestionusuarios.repository.SystemUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemUserServiceTest {

    @Mock private SystemUserRepository systemUserRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private SystemUserService systemUserService;

    private Role role;
    private SystemUser user;

    @BeforeEach
    void setUp() {
        role = Role.builder().id(1).name("ADMIN").description("Admin").status(Status.ACTIVE).build();
        user = SystemUser.builder()
                .id(1).idNumber("1234567890").name("John").email("john@test.com")
                .password("encoded").role(role).active(true)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        when(systemUserRepository.findAll()).thenReturn(List.of(user));
        List<UserResponse> result = systemUserService.findAll();
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getName());
    }

    @Test
    void findById_ShouldReturnUser() {
        when(systemUserRepository.findById(1)).thenReturn(Optional.of(user));
        UserResponse result = systemUserService.findById(1);
        assertEquals("John", result.getName());
    }

    @Test
    void findById_ShouldThrowWhenNotFound() {
        when(systemUserRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> systemUserService.findById(99));
    }

    @Test
    void create_ShouldSaveAndReturnUser() {
        UserRequest req = UserRequest.builder()
                .idNumber("9876543210").name("Jane").email("jane@test.com")
                .password("pass123").roleId(1).build();

        when(systemUserRepository.existsByIdNumber("9876543210")).thenReturn(false);
        when(systemUserRepository.existsByEmail("jane@test.com")).thenReturn(false);
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(systemUserRepository.save(any(SystemUser.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse result = systemUserService.create(req);
        assertEquals("Jane", result.getName());
        assertEquals("ADMIN", result.getRoleName());
        assertEquals("ACTIVE", result.getStatus());
        assertTrue(result.getActive());
    }

    @Test
    void create_ShouldThrowOnDuplicateIdNumber() {
        UserRequest req = UserRequest.builder().idNumber("1234567890").name("Jane").email("jane@test.com").password("p").roleId(1).build();
        when(systemUserRepository.existsByIdNumber("1234567890")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> systemUserService.create(req));
    }

    @Test
    void create_ShouldThrowOnDuplicateEmail() {
        UserRequest req = UserRequest.builder().idNumber("9876543210").name("Jane").email("john@test.com").password("p").roleId(1).build();
        when(systemUserRepository.existsByIdNumber("9876543210")).thenReturn(false);
        when(systemUserRepository.existsByEmail("john@test.com")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> systemUserService.create(req));
    }

    @Test
    void create_ShouldThrowWhenPasswordMissing() {
        UserRequest req = UserRequest.builder().idNumber("9876543210").name("Jane").email("jane@test.com").password("").roleId(1).build();
        when(systemUserRepository.existsByIdNumber("9876543210")).thenReturn(false);
        when(systemUserRepository.existsByEmail("jane@test.com")).thenReturn(false);
        assertThrows(BadRequestException.class, () -> systemUserService.create(req));
    }

    @Test
    void update_ShouldModifyUser() {
        UserRequest req = UserRequest.builder().idNumber("1234567890").name("John Updated").email("john@test.com").roleId(1).build();
        when(systemUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(systemUserRepository.save(any(SystemUser.class))).thenReturn(user);

        UserResponse result = systemUserService.update(1, req);
        assertEquals("John Updated", result.getName());
    }

    @Test
    void update_ShouldThrowWhenNotFound() {
        UserRequest req = UserRequest.builder().idNumber("x").name("x").email("x@x.com").roleId(1).build();
        when(systemUserRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> systemUserService.update(99, req));
    }

    @Test
    void deactivate_ShouldSetActiveFalse() {
        when(systemUserRepository.findById(1)).thenReturn(Optional.of(user));
        systemUserService.deactivate(1);
        assertFalse(user.getActive());
        verify(systemUserRepository).save(user);
    }

    @Test
    void reactivate_ShouldSetActiveTrue() {
        user.setActive(false);
        when(systemUserRepository.findById(1)).thenReturn(Optional.of(user));
        systemUserService.reactivate(1);
        assertTrue(user.getActive());
        verify(systemUserRepository).save(user);
    }
}