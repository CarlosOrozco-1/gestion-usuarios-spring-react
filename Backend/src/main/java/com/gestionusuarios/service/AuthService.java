package com.gestionusuarios.service;

import com.gestionusuarios.dto.request.LoginRequest;
import com.gestionusuarios.dto.response.LoginResponse;
import com.gestionusuarios.dto.response.UserResponse;
import com.gestionusuarios.entity.User;
import com.gestionusuarios.exception.UnauthorizedException;
import com.gestionusuarios.repository.UserRepository;
import com.gestionusuarios.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${jwt.expiration-hours}")
    private long expirationHours;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid credentials");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(userDetails);

        User user = userRepository.findByEmailAndActiveTrue(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        return LoginResponse.builder()
                .token(token)
                .expiresIn(expirationHours * 3600)
                .user(toUserResponse(user))
                .build();
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .idNumber(user.getIdNumber())
                .name(user.getName())
                .email(user.getEmail())
                .roleName(user.getRole().getName())
                .status(user.getStatus().name())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
