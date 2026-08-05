package com.gestionusuarios.controller;

import com.gestionusuarios.dto.request.ClientRequest;
import com.gestionusuarios.dto.response.ClientResponse;
import com.gestionusuarios.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_CLIENTS')")
    public ResponseEntity<List<ClientResponse>> findAll() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_CLIENTS')")
    public ResponseEntity<ClientResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_CLIENTS')")
    public ResponseEntity<ClientResponse> create(@RequestBody @Valid ClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_CLIENTS')")
    public ResponseEntity<ClientResponse> update(@PathVariable Integer id, @RequestBody @Valid ClientRequest request) {
        return ResponseEntity.ok(clientService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_CLIENTS')")
    public ResponseEntity<Void> deactivate(@PathVariable Integer id) {
        clientService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivate")
    @PreAuthorize("hasAuthority('MANAGE_CLIENTS')")
    public ResponseEntity<Void> reactivate(@PathVariable Integer id) {
        clientService.reactivate(id);
        return ResponseEntity.ok().build();
    }
}