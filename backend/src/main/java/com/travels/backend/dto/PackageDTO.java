package com.travels.backend.dto;

import com.travels.backend.model.PackageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageDTO {

    private Long id;

    private String name;

    private String description;

    private Double price;

    private String destination;

    private String itinerary;

    private String images;

    private PackageStatus status;

    private Double averageRating;

    private Long viewCount;

    private Long bookingCount;
}
