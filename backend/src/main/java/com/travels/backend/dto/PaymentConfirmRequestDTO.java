package com.travels.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentConfirmRequestDTO {

    @NotNull(message = "El ID de la reserva es obligatorio")
    private Long bookingId;
}
