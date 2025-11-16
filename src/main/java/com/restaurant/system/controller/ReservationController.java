package com.restaurant.system.controller;

import com.restaurant.system.dto.ReservationDTO;
import com.restaurant.system.entity.enums.ReservationStatus;
import com.restaurant.system.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@Valid  @RequestBody ReservationDTO reservationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(reservationDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(reservationService.getReservationsByClientId(clientId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByStatus(@PathVariable ReservationStatus status) {
        return ResponseEntity.ok(reservationService.getReservationsByStatus(status));
    }

    @GetMapping("/available-slots")
    public ResponseEntity<List<ReservationDTO>> getAvailableSlots(
            @RequestParam Long tableId,
            @RequestParam OffsetDateTime startTime,
            @RequestParam OffsetDateTime endTime) {
        return ResponseEntity.ok(reservationService.getAvailableSlots(tableId, startTime, endTime));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationDTO> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationDTO reservationDTO) {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservationDTO));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
