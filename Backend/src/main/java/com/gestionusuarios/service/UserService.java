package com.gestionusuarios.service;

import com.gestionusuarios.dto.request.UserRequest;
import com.gestionusuarios.dto.response.UserResponse;
import com.gestionusuarios.entity.Role;
import com.gestionusuarios.entity.Status;
import com.gestionusuarios.entity.User;
import com.gestionusuarios.exception.BadRequestException;
import com.gestionusuarios.exception.DuplicateResourceException;
import com.gestionusuarios.exception.ResourceNotFoundException;
import com.gestionusuarios.repository.RoleRepository;
import com.gestionusuarios.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .filter(User::getActive)
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return toResponse(user);
    }

    public UserResponse create(UserRequest request) {
        if (userRepository.existsByIdNumber(request.getIdNumber())) {
            throw new DuplicateResourceException("User", "idNumber", request.getIdNumber());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Password is required");
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", request.getRoleId()));

        User user = User.builder()
                .idNumber(request.getIdNumber())
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .status(Status.ACTIVE)
                .active(true)
                .build();

        return toResponse(userRepository.save(user));
    }

    public UserResponse update(Integer id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        if (!user.getIdNumber().equals(request.getIdNumber())
                && userRepository.existsByIdNumber(request.getIdNumber())) {
            throw new DuplicateResourceException("User", "idNumber", request.getIdNumber());
        }
        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
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

        return toResponse(userRepository.save(user));
    }

    public void deactivate(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setActive(false);
        userRepository.save(user);
    }

    public void reactivate(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setActive(true);
        userRepository.save(user);
    }

    private UserResponse toResponse(User user) {
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
