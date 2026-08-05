package com.gestionusuarios.controller;

import com.gestionusuarios.dto.request.CredentialRequest;
import com.gestionusuarios.dto.response.CredentialResponse;
import com.gestionusuarios.service.CredentialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credentials")
@RequiredArgsConstructor
public class CredentialController {

    private final CredentialService credentialService;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_CREDENTIALS')")
    public ResponseEntity<List<CredentialResponse>> findAll() {
        return ResponseEntity.ok(credentialService.findAll());
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAuthority('VIEW_CREDENTIALS')")
    public ResponseEntity<List<CredentialResponse>> findByClientId(@PathVariable Integer clientId) {
        return ResponseEntity.ok(credentialService.findByClientId(clientId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_CREDENTIALS')")
    public ResponseEntity<CredentialResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(credentialService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_CREDENTIALS')")
    public ResponseEntity<CredentialResponse> create(@RequestBody @Valid CredentialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(credentialService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_CREDENTIALS')")
    public ResponseEntity<CredentialResponse> update(@PathVariable Integer id, @RequestBody @Valid CredentialRequest request) {
        return ResponseEntity.ok(credentialService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_CREDENTIALS')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        credentialService.delete(id);
        return ResponseEntity.noContent().build();
    }
}