package com.restaurant.system.dto;

import com.restaurant.system.entity.enums.ReservationStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Reservation time is required")
    private OffsetDateTime reservationTime;

    @Min(value = 15, message = "Duration must be at least 15 minutes")
    private Integer durationMinutes;

    @Min(value = 1, message = "Party size must be at least 1")
    private Integer partySize;

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Table ID is required")
    private Long tableId;

    private String tableNumber;

    private ReservationStatus status;

    private String notes;

    private OffsetDateTime createdAt;
}
