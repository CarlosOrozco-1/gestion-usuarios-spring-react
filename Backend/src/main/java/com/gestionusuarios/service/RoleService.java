package com.gestionusuarios.service;

import com.gestionusuarios.dto.request.RoleRequest;
import com.gestionusuarios.dto.response.PermissionResponse;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<RoleResponse> findAll() {
        return roleRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleResponse findById(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        return toResponse(role);
    }

    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Role", "name", request.getName());
        }

        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(Status.ACTIVE)
                .build();

        return toResponse(roleRepository.save(role));
    }

    public RoleResponse update(Integer id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));

        if (!role.getName().equals(request.getName())
                && roleRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Role", "name", request.getName());
        }

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        return toResponse(roleRepository.save(role));
    }

    public void delete(Integer id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", id);
        }
        roleRepository.deleteById(id);
    }

    public RoleResponse assignPermissions(Integer roleId, List<Integer> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", roleId));

        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        if (permissions.size() != permissionIds.size()) {
            throw new BadRequestException("One or more permissions not found");
        }

        role.getRolePermissions().clear();
        for (Permission permission : permissions) {
            PermissionRole pr = PermissionRole.builder()
                    .role(role)
                    .permission(permission)
                    .build();
            role.getRolePermissions().add(pr);
        }

        return toResponse(roleRepository.save(role));
    }

    private RoleResponse toResponse(Role role) {
        List<PermissionResponse> permissions = role.getRolePermissions().stream()
                .map(pr -> PermissionResponse.builder()
                        .id(pr.getPermission().getId())
                        .name(pr.getPermission().getName())
                        .description(pr.getPermission().getDescription())
                        .resourcePath(pr.getPermission().getResourcePath())
                        .createdAt(pr.getPermission().getCreatedAt())
                        .build())
                .toList();

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .status(role.getStatus().name())
                .createdAt(role.getCreatedAt())
                .permissions(permissions)
                .build();
    }
}
