package com.restaurant.system.service;

import com.restaurant.system.dto.ClientDTO;
import java.util.List;
import java.util.Optional;

public interface ClientService {
    ClientDTO createClient(ClientDTO clientDTO);
    Optional<ClientDTO> getClientById(Long id);
    List<ClientDTO> getAllClients();
    ClientDTO updateClient(Long id, ClientDTO clientDTO);
    void deleteClient(Long id);
    Optional<ClientDTO> getClientByEmail(String email);
}
