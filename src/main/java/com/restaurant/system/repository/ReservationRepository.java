package com.restaurant.system.repository;

import com.restaurant.system.entity.Reservation;
import com.restaurant.system.entity.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Page<Reservation> findByClient_Id(Long clientId, Pageable pageable);
    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);

    @Query(value = "SELECT * FROM reservations r " +
            "WHERE r.table_id = :tableId " +
            "AND r.status = 'ACTIVE' " +
            "AND r.reservation_time < :endTime " +
            "AND (r.reservation_time + (r.duration_minutes * INTERVAL '1 minute')) > :startTime",
            nativeQuery = true)
    List<Reservation> findActiveReservationsForTable(
            @Param("tableId") Long tableId,
            @Param("startTime") OffsetDateTime startTime,
            @Param("endTime") OffsetDateTime endTime
    );

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.status = :status")
    Long countByStatus(@Param("status") ReservationStatus status);
}
