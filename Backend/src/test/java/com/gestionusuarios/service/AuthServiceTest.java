package com.gestionusuarios.service;

import com.gestionusuarios.dto.request.LoginRequest;
import com.gestionusuarios.dto.response.LoginResponse;
import com.gestionusuarios.entity.Role;
import com.gestionusuarios.entity.Status;
import com.gestionusuarios.entity.SystemUser;
import com.gestionusuarios.exception.UnauthorizedException;
import com.gestionusuarios.repository.SystemUserRepository;
import com.gestionusuarios.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private SystemUserRepository systemUserRepository;
    @InjectMocks private AuthService authService;

    private SystemUser user;

    @BeforeEach
    void setUp() {
        Role role = Role.builder().id(1).name("ADMIN").status(Status.ACTIVE).build();
        user = SystemUser.builder()
                .id(1).idNumber("123").name("Admin").email("admin@test.com")
                .password("encoded").role(role).active(true)
                .createdAt(LocalDateTime.now())
                .build();

        ReflectionTestUtils.setField(authService, "expirationHours", 8L);
    }

    @Test
    void login_ShouldReturnToken() {
        LoginRequest req = new LoginRequest("admin@test.com", "pass123");

        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(userDetails)).thenReturn("token123");
        when(systemUserRepository.findByEmailAndActiveTrue("admin@test.com")).thenReturn(Optional.of(user));

        LoginResponse result = authService.login(req);
        assertEquals("token123", result.getToken());
        assertEquals(28800, result.getExpiresIn());
        assertEquals("admin@test.com", result.getUser().getEmail());
    }

    @Test
    void login_ShouldThrowOnBadCredentials() {
        LoginRequest req = new LoginRequest("admin@test.com", "wrong");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(UnauthorizedException.class, () -> authService.login(req));
    }
}