package com.gestionusuarios.service;

import com.gestionusuarios.dto.request.LoginRequest;
import com.gestionusuarios.dto.response.LoginResponse;
import com.gestionusuarios.entity.SystemUser;
import com.gestionusuarios.exception.UnauthorizedException;
import com.gestionusuarios.mapper.UserMapper;
import com.gestionusuarios.repository.SystemUserRepository;
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
    private final SystemUserRepository systemUserRepository;

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

        SystemUser user = systemUserRepository.findByEmailAndActiveTrue(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        return LoginResponse.builder()
                .token(token)
                .expiresIn(expirationHours * 3600)
                .user(UserMapper.toResponse(user))
                .build();
    }
}