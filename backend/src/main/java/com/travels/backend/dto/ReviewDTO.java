package com.travels.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {

    private Long id;

    private UserDTO user;

    private PackageDTO travelPackage;

    private Integer rating;

    private String comment;

    private Boolean verified;
}
