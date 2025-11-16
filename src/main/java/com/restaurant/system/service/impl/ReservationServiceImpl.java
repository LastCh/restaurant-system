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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
            throw new IllegalArgumentException("Reservation time cannot be empty");
        }

        Client client = clientRepository.findById(reservationDTO.getClientId())
                .orElseThrow(() -> new NotFoundException("Client not found"));

        RestaurantTable table = tableRepository.findById(reservationDTO.getTableId())
                .orElseThrow(() -> new NotFoundException("Table not found"));

        List<Reservation> conflictingReservations = reservationRepository
                .findActiveReservationsForTable(
                        reservationDTO.getTableId(),
                        reservationDTO.getReservationTime(),
                        reservationDTO.getReservationTime().plusMinutes(reservationDTO.getDurationMinutes())
                );

        if (!conflictingReservations.isEmpty()) {
            throw new IllegalArgumentException("The table is occupied at the indicated time");
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
    public Page<ReservationDTO> getAllReservations(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return reservationRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDTO> getReservationsByClientId(Long clientId, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return reservationRepository.findByClient_Id(clientId, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDTO> getReservationsByStatus(ReservationStatus status, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return reservationRepository.findByStatus(status, pageable).map(this::toDTO);
    }

    @Override
    public ReservationDTO updateReservation(Long id, ReservationDTO reservationDTO) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservations not found"));

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
            throw new NotFoundException("Reservations not found");
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
                    .orElseThrow(() -> new NotFoundException("Table not found"));

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
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

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
