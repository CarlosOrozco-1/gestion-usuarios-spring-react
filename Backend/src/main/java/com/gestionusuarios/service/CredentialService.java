package com.gestionusuarios.service;

import com.gestionusuarios.dto.request.CredentialRequest;
import com.gestionusuarios.dto.response.CredentialResponse;
import com.gestionusuarios.entity.Credential;
import com.gestionusuarios.entity.Client;
import com.gestionusuarios.exception.ResourceNotFoundException;
import com.gestionusuarios.mapper.CredentialMapper;
import com.gestionusuarios.repository.CredentialRepository;
import com.gestionusuarios.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public List<CredentialResponse> findAll() {
        return credentialRepository.findAll().stream()
                .map(CredentialMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CredentialResponse> findByClientId(Integer clientId) {
        return credentialRepository.findByClientId(clientId).stream()
                .map(CredentialMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CredentialResponse findById(Integer id) {
        Credential credential = credentialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credential", id));
        return CredentialMapper.toResponse(credential);
    }

    public CredentialResponse create(CredentialRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", request.getClientId()));

        Credential credential = Credential.builder()
                .client(client)
                .systemName(request.getSystemName())
                .username(request.getUsername())
                .encryptedPassword(request.getEncryptedPassword())
                .url(request.getUrl())
                .notes(request.getNotes())
                .build();

        return CredentialMapper.toResponse(credentialRepository.save(credential));
    }

    public CredentialResponse update(Integer id, CredentialRequest request) {
        Credential credential = credentialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credential", id));

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", request.getClientId()));

        credential.setClient(client);
        credential.setSystemName(request.getSystemName());
        credential.setUsername(request.getUsername());
        credential.setEncryptedPassword(request.getEncryptedPassword());
        credential.setUrl(request.getUrl());
        credential.setNotes(request.getNotes());

        return CredentialMapper.toResponse(credentialRepository.save(credential));
    }

    public void delete(Integer id) {
        if (!credentialRepository.existsById(id)) {
            throw new ResourceNotFoundException("Credential", id);
        }
        credentialRepository.deleteById(id);
    }
}