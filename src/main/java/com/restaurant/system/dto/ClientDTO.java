package com.restaurant.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private Long id;

    @NotBlank(message = "Full name is required")
    @Pattern(
            regexp = "^[A-Za-zА-Яа-яёЁ ]+$",
            message = "Name should contain only letters and spaces"
    )
    private String fullName;

    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Phone number should be in E.164 format (e.g., +79367087110)"
    )
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    private OffsetDateTime createdAt;
}
