package com.restaurant.system.service;

import com.restaurant.system.dto.ReservationDTO;
import com.restaurant.system.entity.enums.ReservationStatus;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationService {
    ReservationDTO createReservation(ReservationDTO reservationDTO);
    Optional<ReservationDTO> getReservationById(Long id);
    List<ReservationDTO> getAllReservations();
    List<ReservationDTO> getReservationsByClientId(Long clientId);
    List<ReservationDTO> getReservationsByStatus(ReservationStatus status);
    ReservationDTO updateReservation(Long id, ReservationDTO reservationDTO);
    void deleteReservation(Long id);

    // Специфичные методы
    List<ReservationDTO> getAvailableSlots(Long tableId, OffsetDateTime startTime, OffsetDateTime endTime);
    void cancelReservation(Long id);
}
