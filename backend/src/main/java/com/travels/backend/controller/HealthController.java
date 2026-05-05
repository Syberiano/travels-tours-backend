package com.travels.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Salud", description = "Comprobación de disponibilidad del servicio")
public class HealthController {

    @Value("${spring.application.name:backend}")
    private String applicationName;

    @GetMapping("/health")
    @Operation(summary = "Estado del servicio", description = "Indica si la API está en ejecución (sin base de datos).")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "application", applicationName,
                "timestamp", Instant.now().toString()
        ));
    }
}
