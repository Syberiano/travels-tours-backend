package com.travels.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    private String title;

    @NotBlank(message = "El contenido es obligatorio")
    private String content;

    private String featuredImage;
}
