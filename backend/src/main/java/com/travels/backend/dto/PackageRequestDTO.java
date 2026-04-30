package com.travels.backend.dto;

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

    private String itinerary;

    private String images;
}
