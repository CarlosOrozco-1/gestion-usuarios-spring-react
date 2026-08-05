package com.gestionusuarios.controller;

import com.gestionusuarios.dto.request.PermissionRequest;
import com.gestionusuarios.dto.response.PermissionResponse;
import com.gestionusuarios.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN_PERMISSIONS')")
    public ResponseEntity<List<PermissionResponse>> findAll() {
        return ResponseEntity.ok(permissionService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_PERMISSIONS')")
    public ResponseEntity<PermissionResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(permissionService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN_PERMISSIONS')")
    public ResponseEntity<PermissionResponse> create(@RequestBody @Valid PermissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_PERMISSIONS')")
    public ResponseEntity<PermissionResponse> update(@PathVariable Integer id, @RequestBody @Valid PermissionRequest request) {
        return ResponseEntity.ok(permissionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_PERMISSIONS')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}