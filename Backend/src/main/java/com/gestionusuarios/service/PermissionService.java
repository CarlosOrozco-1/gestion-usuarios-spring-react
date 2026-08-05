package com.gestionusuarios.service;

import com.gestionusuarios.dto.request.PermissionRequest;
import com.gestionusuarios.dto.response.PermissionResponse;
import com.gestionusuarios.entity.Permission;
import com.gestionusuarios.exception.DuplicateResourceException;
import com.gestionusuarios.exception.ResourceNotFoundException;
import com.gestionusuarios.mapper.PermissionMapper;
import com.gestionusuarios.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<PermissionResponse> findAll() {
        return permissionRepository.findAll().stream()
                .map(PermissionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PermissionResponse findById(Integer id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", id));
        return PermissionMapper.toResponse(permission);
    }

    public PermissionResponse create(PermissionRequest permissionRequest) {
        if (permissionRepository.existsByName(permissionRequest.getName())) {
            throw new DuplicateResourceException("Permission", "name", permissionRequest.getName());
        }

        Permission permission = Permission.builder()
                .name(permissionRequest.getName())
                .description(permissionRequest.getDescription())
                .resourcePath(permissionRequest.getResourcePath())
                .build();

        return PermissionMapper.toResponse(permissionRepository.save(permission));
    }

    public PermissionResponse update(Integer id, PermissionRequest permissionRequest) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", id));

        if (!permission.getName().equals(permissionRequest.getName())
                && permissionRepository.existsByName(permissionRequest.getName())) {
            throw new DuplicateResourceException("Permission", "name", permissionRequest.getName());
        }

        permission.setName(permissionRequest.getName());
        permission.setDescription(permissionRequest.getDescription());
        permission.setResourcePath(permissionRequest.getResourcePath());

        return PermissionMapper.toResponse(permissionRepository.save(permission));
    }

    public void delete(Integer id) {
        if (!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Permission", id);
        }
        permissionRepository.deleteById(id);
    }
}
