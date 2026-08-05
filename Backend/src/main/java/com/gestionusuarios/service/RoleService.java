package com.gestionusuarios.service;

import com.gestionusuarios.dto.request.RoleRequest;
import com.gestionusuarios.dto.response.RoleResponse;
import com.gestionusuarios.entity.Permission;
import com.gestionusuarios.entity.PermissionRole;
import com.gestionusuarios.entity.Role;
import com.gestionusuarios.entity.Status;
import com.gestionusuarios.exception.BadRequestException;
import com.gestionusuarios.exception.DuplicateResourceException;
import com.gestionusuarios.exception.ResourceNotFoundException;
import com.gestionusuarios.mapper.RoleMapper;
import com.gestionusuarios.repository.PermissionRepository;
import com.gestionusuarios.repository.RoleRepository;
import com.gestionusuarios.repository.SystemUserRepository;
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
    private final SystemUserRepository systemUserRepository;

    @Transactional(readOnly = true)
    public List<RoleResponse> findAll() {
        return roleRepository.findAll().stream()
                .map(RoleMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleResponse findById(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        return RoleMapper.toResponse(role);
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

        return RoleMapper.toResponse(roleRepository.save(role));
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

        return RoleMapper.toResponse(roleRepository.save(role));
    }

    public void delete(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));

        if (systemUserRepository.existsByRoleId(id)) {
            throw new BadRequestException("Cannot delete role because it has assigned system users");
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

        return RoleMapper.toResponse(roleRepository.save(role));
    }
}