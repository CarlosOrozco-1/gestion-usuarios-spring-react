package com.gestionusuarios.controller;

import com.gestionusuarios.dto.request.PermissionRequest;
import com.gestionusuarios.dto.response.PermissionResponse;
import com.gestionusuarios.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public ResponseEntity<List<PermissionResponse>> findAll() {
        return ResponseEntity.ok(permissionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(permissionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PermissionResponse> create(@RequestBody @Valid PermissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponse> update(@PathVariable Integer id, @RequestBody @Valid PermissionRequest request) {
        return ResponseEntity.ok(permissionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
