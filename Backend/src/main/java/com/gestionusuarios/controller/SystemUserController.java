package com.gestionusuarios.controller;

import com.gestionusuarios.dto.request.UserRequest;
import com.gestionusuarios.dto.response.UserResponse;
import com.gestionusuarios.service.SystemUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system-users")
@RequiredArgsConstructor
public class SystemUserController {

    private final SystemUserService systemUserService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN_USERS')")
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(systemUserService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_USERS')")
    public ResponseEntity<UserResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(systemUserService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN_USERS')")
    public ResponseEntity<UserResponse> create(@RequestBody @Valid UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(systemUserService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_USERS')")
    public ResponseEntity<UserResponse> update(@PathVariable Integer id, @RequestBody @Valid UserRequest request) {
        return ResponseEntity.ok(systemUserService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_USERS')")
    public ResponseEntity<Void> deactivate(@PathVariable Integer id) {
        systemUserService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivate")
    @PreAuthorize("hasAuthority('ADMIN_USERS')")
    public ResponseEntity<Void> reactivate(@PathVariable Integer id) {
        systemUserService.reactivate(id);
        return ResponseEntity.ok().build();
    }
}