package com.gestionusuarios.service;

import com.gestionusuarios.dto.request.ClientRequest;
import com.gestionusuarios.dto.response.ClientResponse;
import com.gestionusuarios.entity.Client;
import com.gestionusuarios.exception.BadRequestException;
import com.gestionusuarios.exception.DuplicateResourceException;
import com.gestionusuarios.exception.ResourceNotFoundException;
import com.gestionusuarios.mapper.ClientMapper;
import com.gestionusuarios.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public List<ClientResponse> findAll() {
        return clientRepository.findAll().stream()
                .map(ClientMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClientResponse findById(Integer id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        return ClientMapper.toResponse(client);
    }

    public ClientResponse create(ClientRequest request) {
        if (clientRepository.existsByIdNumber(request.getIdNumber())) {
            throw new DuplicateResourceException("Client", "idNumber", request.getIdNumber());
        }
        if (clientRepository.existsByNit(request.getNit())) {
            throw new DuplicateResourceException("Client", "nit", request.getNit());
        }
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Client", "email", request.getEmail());
        }

        Client client = Client.builder()
                .idNumber(request.getIdNumber())
                .nit(request.getNit())
                .businessName(request.getBusinessName())
                .taxRegime(request.getTaxRegime())
                .birthDate(request.getBirthDate())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .active(true)
                .build();

        return ClientMapper.toResponse(clientRepository.save(client));
    }

    public ClientResponse update(Integer id, ClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));

        if (!client.getIdNumber().equals(request.getIdNumber())
                && clientRepository.existsByIdNumber(request.getIdNumber())) {
            throw new DuplicateResourceException("Client", "idNumber", request.getIdNumber());
        }
        if (!client.getNit().equals(request.getNit())
                && clientRepository.existsByNit(request.getNit())) {
            throw new DuplicateResourceException("Client", "nit", request.getNit());
        }
        if (!client.getEmail().equals(request.getEmail())
                && clientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Client", "email", request.getEmail());
        }

        client.setIdNumber(request.getIdNumber());
        client.setNit(request.getNit());
        client.setBusinessName(request.getBusinessName());
        client.setTaxRegime(request.getTaxRegime());
        client.setBirthDate(request.getBirthDate());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        client.setAddress(request.getAddress());

        return ClientMapper.toResponse(clientRepository.save(client));
    }

    public void deactivate(Integer id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        client.setActive(false);
        clientRepository.save(client);
    }

    public void reactivate(Integer id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        client.setActive(true);
        clientRepository.save(client);
    }
}