package com.travels.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDTO {

    @NotNull(message = "El ID del paquete es obligatorio")
    private Long packageId;

    @NotNull(message = "La fecha del viaje es obligatoria")
    private LocalDate travelDate;

    @Positive(message = "El número de participantes debe ser mayor a 0")
    private Integer numberOfParticipants;

    private String specialRequests;
}
