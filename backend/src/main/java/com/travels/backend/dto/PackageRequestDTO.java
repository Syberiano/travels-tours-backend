package com.travels.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageRequestDTO {

    @NotBlank(message = "El nombre del paquete es obligatorio")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @Positive(message = "El precio debe ser mayor a 0")
    private Double price;

    @NotBlank(message = "El destino es obligatorio")
    private String destination;

    @Min(value = 1, message = "La duración en días debe ser al menos 1")
    private int durationDays;

    @Min(value = 1, message = "El máximo de participantes debe ser al menos 1")
    private int maxParticipants;

    private String itinerary;

    private String images;
}
