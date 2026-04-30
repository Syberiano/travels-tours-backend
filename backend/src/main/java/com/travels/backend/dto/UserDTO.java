package com.travels.backend.dto;

import com.travels.backend.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private String phone;
    private String profileImage;
    private String bio;
    private Boolean active;
}
