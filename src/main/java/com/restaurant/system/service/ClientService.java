package com.restaurant.system.service;

import com.restaurant.system.dto.ClientDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    ClientDTO createClient(ClientDTO clientDTO);

    Optional<ClientDTO> getClientById(Long id);

    Optional<ClientDTO> getClientByEmail(String email);

    Page<ClientDTO> getAllClients(int page, int size, String sortBy, String direction);

    ClientDTO updateClient(Long id, ClientDTO clientDTO);

    void deleteClient(Long id);
}
