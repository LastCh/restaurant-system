package com.restaurant.system.service;

import com.restaurant.system.dto.ReservationDTO;
import com.restaurant.system.entity.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationService {
    ReservationDTO createReservation(ReservationDTO reservationDTO);

    Optional<ReservationDTO> getReservationById(Long id);

    Page<ReservationDTO> getAllReservations(int page, int size, String sortBy, String direction);

    Page<ReservationDTO> getReservationsByClientId(Long clientId, int page, int size, String sortBy, String direction);

    Page<ReservationDTO> getReservationsByStatus(ReservationStatus status, int page, int size, String sortBy, String direction);

    ReservationDTO updateReservation(Long id, ReservationDTO reservationDTO);

    List<ReservationDTO> getAvailableSlots(Long tableId, OffsetDateTime startTime, OffsetDateTime endTime);

    void deleteReservation(Long id);

    void cancelReservation(Long id);

}
