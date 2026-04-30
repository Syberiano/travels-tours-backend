package com.travels.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDTO {

    @NotNull(message = "El ID del booking es obligatorio")
    private Long bookingId;

    @Min(value = 1, message = "La calificación debe ser mínimo 1")
    @Max(value = 5, message = "La calificación debe ser máximo 5")
    private Integer rating;

    private String comment;
}
