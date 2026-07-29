package com.gestionusuarios.controller;

import com.gestionusuarios.dto.request.RoleRequest;
import com.gestionusuarios.dto.response.RoleResponse;
import com.gestionusuarios.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleResponse>> findAll() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(roleService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RoleResponse> create(@RequestBody @Valid RoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> update(@PathVariable Integer id, @RequestBody @Valid RoleRequest request) {
        return ResponseEntity.ok(roleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/permissions")
    public ResponseEntity<RoleResponse> assignPermissions(@PathVariable Integer id, @RequestBody List<Integer> permissionIds) {
        return ResponseEntity.ok(roleService.assignPermissions(id, permissionIds));
    }
}
