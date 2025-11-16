package com.restaurant.system.service.impl;

import com.restaurant.system.dto.ReservationDTO;
import com.restaurant.system.entity.Reservation;
import com.restaurant.system.entity.RestaurantTable;
import com.restaurant.system.entity.Client;
import com.restaurant.system.entity.enums.ReservationStatus;
import com.restaurant.system.exception.NotFoundException;
import com.restaurant.system.repository.ReservationRepository;
import com.restaurant.system.repository.RestaurantTableRepository;
import com.restaurant.system.repository.ClientRepository;
import com.restaurant.system.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository tableRepository;
    private final ClientRepository clientRepository;

    @Override
    public ReservationDTO createReservation(ReservationDTO reservationDTO) {
        if (reservationDTO.getReservationTime() == null) {
            throw new IllegalArgumentException("Время бронирования не может быть пустым");
        }

        Client client = clientRepository.findById(reservationDTO.getClientId())
                .orElseThrow(() -> new NotFoundException("Клиент не найден"));

        RestaurantTable table = tableRepository.findById(reservationDTO.getTableId())
                .orElseThrow(() -> new NotFoundException("Стол не найден"));

        List<Reservation> conflictingReservations = reservationRepository
                .findActiveReservationsForTable(
                        reservationDTO.getTableId(),
                        reservationDTO.getReservationTime(),
                        reservationDTO.getReservationTime().plusMinutes(reservationDTO.getDurationMinutes())
                );

        if (!conflictingReservations.isEmpty()) {
            throw new IllegalArgumentException("Стол занят в указанное время");
        }

        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setTable(table);
        reservation.setReservationTime(reservationDTO.getReservationTime());
        reservation.setDurationMinutes(reservationDTO.getDurationMinutes() != null ?
                reservationDTO.getDurationMinutes() : 90);
        reservation.setPartySize(reservationDTO.getPartySize() != null ?
                reservationDTO.getPartySize() : 1);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setNotes(reservationDTO.getNotes());

        return toDTO(reservationRepository.save(reservation));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReservationDTO> getReservationById(Long id) {
        return reservationRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsByClientId(Long clientId) {
        return reservationRepository.findByClientId(clientId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationDTO updateReservation(Long id, ReservationDTO reservationDTO) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (reservationDTO.getReservationTime() != null) {
            reservation.setReservationTime(reservationDTO.getReservationTime());
        }
        if (reservationDTO.getDurationMinutes() != null) {
            reservation.setDurationMinutes(reservationDTO.getDurationMinutes());
        }
        if (reservationDTO.getPartySize() != null) {
            reservation.setPartySize(reservationDTO.getPartySize());
        }
        if (reservationDTO.getNotes() != null) {
            reservation.setNotes(reservationDTO.getNotes());
        }

        return toDTO(reservationRepository.save(reservation));
    }

    @Override
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new NotFoundException("Бронирование не найдено");
        }
        reservationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDTO> getAvailableSlots(Long tableId, OffsetDateTime startTime, OffsetDateTime endTime) {
        List<Reservation> conflicting = reservationRepository
                .findActiveReservationsForTable(tableId, startTime, endTime);

        if (conflicting.isEmpty()) {
            RestaurantTable table = tableRepository.findById(tableId)
                    .orElseThrow(() -> new NotFoundException("Стол не найден"));

            Reservation freeSlot = new Reservation();
            freeSlot.setTable(table);
            freeSlot.setReservationTime(startTime);
            freeSlot.setStatus(ReservationStatus.ACTIVE);

            return List.of(toDTO(freeSlot));
        }

        return List.of();
    }

    @Override
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    private ReservationDTO toDTO(Reservation reservation) {
        return ReservationDTO.builder()
                .id(reservation.getId())
                .reservationTime(reservation.getReservationTime())
                .durationMinutes(reservation.getDurationMinutes())
                .partySize(reservation.getPartySize())
                .clientId(reservation.getClient().getId())
                .tableId(reservation.getTable().getId())
                .tableNumber(reservation.getTable().getTableNumber())
                .status(reservation.getStatus())
                .notes(reservation.getNotes())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}
