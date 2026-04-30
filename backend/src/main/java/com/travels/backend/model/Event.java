package com.travels.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @NotNull(message = "El paquete es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "package_id", nullable = false)
    private Package travelPackage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private String userIp;

    private String userAgent;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
