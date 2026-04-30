package com.travels.backend.dto;

import com.travels.backend.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {

    private Long id;

    private UserDTO user;

    private PackageDTO travelPackage;

    private BookingStatus status;

    private Double totalPrice;

    private Integer numberOfParticipants;

    private String specialRequests;
}
