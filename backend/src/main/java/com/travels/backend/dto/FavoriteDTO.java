package com.travels.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteDTO {

    private Long id;

    private UserDTO user;

    private PackageDTO travelPackage;
}
