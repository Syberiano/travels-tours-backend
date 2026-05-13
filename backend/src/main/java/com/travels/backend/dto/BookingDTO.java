package com.travels.backend.dto;

import com.travels.backend.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {

    private Long id;

    private UserDTO user;

    private PackageDTO travelPackage;

    private BookingStatus status;

    private LocalDate travelDate;

    private Double totalPrice;

    private Integer numberOfParticipants;

    private String specialRequests;
}
