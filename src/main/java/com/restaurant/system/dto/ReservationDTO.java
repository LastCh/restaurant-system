package com.restaurant.system.dto;

import com.restaurant.system.entity.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Long id;
    private OffsetDateTime reservationTime;
    private Integer durationMinutes;
    private Integer partySize;
    private Long clientId;
    private Long tableId;
    private String tableNumber;  // Для удобства
    private ReservationStatus status;
    private String notes;
    private OffsetDateTime createdAt;
}
