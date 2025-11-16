package com.restaurant.system.repository;

import com.restaurant.system.entity.Reservation;
import com.restaurant.system.entity.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByClientId(Long clientId);
    List<Reservation> findByStatus(ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.table.id = :tableId " +
            "AND r.status = 'ACTIVE' " +
            "AND r.reservationTime BETWEEN :startTime AND :endTime")
    List<Reservation> findActiveReservationsForTable(
            @Param("tableId") Long tableId,
            @Param("startTime") OffsetDateTime startTime,
            @Param("endTime") OffsetDateTime endTime
    );
}
