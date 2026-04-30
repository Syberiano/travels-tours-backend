package com.travels.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactDTO {

    private Long id;

    private String name;

    private String email;

    private String message;

    private String phone;

    private Boolean resolved;

    private String response;
}
