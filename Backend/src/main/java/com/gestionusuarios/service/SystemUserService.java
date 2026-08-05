package com.gestionusuarios.service;

import com.gestionusuarios.dto.request.UserRequest;
import com.gestionusuarios.dto.response.UserResponse;
import com.gestionusuarios.entity.Role;
import com.gestionusuarios.entity.SystemUser;
import com.gestionusuarios.exception.BadRequestException;
import com.gestionusuarios.exception.DuplicateResourceException;
import com.gestionusuarios.exception.ResourceNotFoundException;
import com.gestionusuarios.mapper.UserMapper;
import com.gestionusuarios.repository.RoleRepository;
import com.gestionusuarios.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SystemUserService {

    private final SystemUserRepository systemUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return systemUserRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Integer id) {
        SystemUser user = systemUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SystemUser", id));
        return UserMapper.toResponse(user);
    }

    public UserResponse create(UserRequest request) {
        if (systemUserRepository.existsByIdNumber(request.getIdNumber())) {
            throw new DuplicateResourceException("SystemUser", "idNumber", request.getIdNumber());
        }
        if (systemUserRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("SystemUser", "email", request.getEmail());
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Password is required");
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", request.getRoleId()));

        SystemUser user = SystemUser.builder()
                .idNumber(request.getIdNumber())
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .active(true)
                .build();

        return UserMapper.toResponse(systemUserRepository.save(user));
    }

    public UserResponse update(Integer id, UserRequest request) {
        SystemUser user = systemUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SystemUser", id));

        if (!user.getIdNumber().equals(request.getIdNumber())
                && systemUserRepository.existsByIdNumber(request.getIdNumber())) {
            throw new DuplicateResourceException("SystemUser", "idNumber", request.getIdNumber());
        }
        if (!user.getEmail().equals(request.getEmail())
                && systemUserRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("SystemUser", "email", request.getEmail());
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", request.getRoleId()));

        user.setIdNumber(request.getIdNumber());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(role);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return UserMapper.toResponse(systemUserRepository.save(user));
    }

    public void deactivate(Integer id) {
        SystemUser user = systemUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SystemUser", id));
        user.setActive(false);
        systemUserRepository.save(user);
    }

    public void reactivate(Integer id) {
        SystemUser user = systemUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SystemUser", id));
        user.setActive(true);
        systemUserRepository.save(user);
    }
}