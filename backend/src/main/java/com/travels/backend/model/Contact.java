package com.travels.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe ser válido")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "El mensaje es obligatorio")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    private String phone;

    @Column(nullable = false)
    @lombok.Builder.Default
    private Boolean resolved = false;

    @Column(columnDefinition = "TEXT")
    private String response;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
