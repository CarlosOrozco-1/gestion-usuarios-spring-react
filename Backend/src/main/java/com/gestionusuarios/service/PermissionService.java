package com.gestionusuarios.service;

import com.gestionusuarios.dto.request.PermissionRequest;
import com.gestionusuarios.dto.response.PermissionResponse;
import com.gestionusuarios.entity.Permission;
import com.gestionusuarios.exception.DuplicateResourceException;
import com.gestionusuarios.exception.ResourceNotFoundException;
import com.gestionusuarios.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<PermissionResponse> findAll() {
        return permissionRepository.findAll().stream()
                .map(this::toPermissionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PermissionResponse findById(Integer id) {
        Optional<Permission> optionalPermission = permissionRepository.findById(id);
        if (optionalPermission.isPresent()) {
            return toPermissionResponse(optionalPermission.get());
        } else {
            throw new ResourceNotFoundException("Permission", id);
        }
    }

    @Transactional
    public PermissionResponse create(PermissionRequest permissionRequest) {
        if (permissionRepository.existsByName(permissionRequest.getName())) {
            throw new DuplicateResourceException("Permission", "name", permissionRequest.getName());
        }

        Permission permission = Permission.builder()
                .name(permissionRequest.getName())
                .description(permissionRequest.getDescription())
                .resourcePath(permissionRequest.getResourcePath())
                .build();

        return toPermissionResponse(permissionRepository.save(permission));
    }

    @Transactional
    public PermissionResponse update(Integer id, PermissionRequest permissionRequest) {
        Optional<Permission> optionalPermission = permissionRepository.findById(id);
        if (optionalPermission.isPresent()) {
            Permission existingPermission = optionalPermission.get();
            if (!existingPermission.getName().equals(permissionRequest.getName())
                    && permissionRepository.existsByName(permissionRequest.getName())) {
                throw new DuplicateResourceException("Permission", "name", permissionRequest.getName());
            }

            existingPermission.setName(permissionRequest.getName());
            existingPermission.setDescription(permissionRequest.getDescription());
            existingPermission.setResourcePath(permissionRequest.getResourcePath());

            return toPermissionResponse(permissionRepository.save(existingPermission));
        } else {
            throw new ResourceNotFoundException("Permission", id);
        }
    }

    @Transactional
    public void delete(Integer id) {
        if (!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Permission", id);
        }
        permissionRepository.deleteById(id);
    }

    private PermissionResponse toPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .resourcePath(permission.getResourcePath())
                .createdAt(permission.getCreatedAt())
                .build();
    }
}
