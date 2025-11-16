package com.restaurant.system.service.impl;

import com.restaurant.system.dto.ClientDTO;
import com.restaurant.system.entity.Client;
import com.restaurant.system.exception.NotFoundException;
import com.restaurant.system.repository.ClientRepository;
import com.restaurant.system.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;

    @Override
    public ClientDTO createClient(ClientDTO clientDTO) {
        Client client = new Client();
        client.setFullName(clientDTO.getFullName());
        client.setPhone(clientDTO.getPhone());
        client.setEmail(clientDTO.getEmail());
        return toDTO(clientRepository.save(client));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClientDTO> getClientById(Long id) {
        return clientRepository.findById(id)
                .map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClientDTO> getClientByEmail(String email) {
        return clientRepository.findByEmail(email)
                .map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + id));
        if (clientDTO.getFullName() != null) client.setFullName(clientDTO.getFullName());
        if (clientDTO.getPhone() != null) client.setPhone(clientDTO.getPhone());
        if (clientDTO.getEmail() != null) client.setEmail(clientDTO.getEmail());
        return toDTO(clientRepository.save(client));
    }

    @Override
    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) throw new NotFoundException("Client not found with id: " + id);
        clientRepository.deleteById(id);
    }

    private ClientDTO toDTO(Client client) {
        return ClientDTO.builder()
                .id(client.getId())
                .fullName(client.getFullName())
                .phone(client.getPhone())
                .email(client.getEmail())
                .createdAt(client.getCreatedAt())
                .build();
    }
}
