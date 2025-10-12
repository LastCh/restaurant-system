package com.restaurant.system.service;

import com.restaurant.system.dto.ClientDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RestaurantFacade {
    private final ClientService clientService;
    private final ReservationService reservationService;
    private final OrderService orderService;

    public RestaurantFacade(ClientService clientService,
                            ReservationService reservationService,
                            OrderService orderService) {
        this.clientService = clientService;
        this.reservationService = reservationService;
        this.orderService = orderService;
    }

    public ClientDTO registerClientWithReservation(ClientDTO clientDTO, LocalDateTime reservationTime) {
        ClientDTO client = clientService.createClient(clientDTO);
        reservationService.createReservation(client.getId(), reservationTime);
        return client;
    }
}
