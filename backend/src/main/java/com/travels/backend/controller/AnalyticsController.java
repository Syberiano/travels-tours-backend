package com.travels.backend.controller;

import com.travels.backend.exception.InvalidOperationException;
import com.travels.backend.model.Event;
import com.travels.backend.model.EventType;
import com.travels.backend.service.EventService;
import com.travels.backend.service.PackageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    private static final DateTimeFormatter[] EVENT_DATE_TIME_FORMATTERS = {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    };

    private final EventService eventService;
    private final PackageService packageService;

    private static LocalDateTime parseQueryDateTime(String value, String paramName) {
        for (DateTimeFormatter formatter : EVENT_DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                // probar siguiente formato
            }
        }
        throw new InvalidOperationException(
                "Formato de fecha inválido para " + paramName + ". Use ISO-8601, por ejemplo 2025-01-15T10:30:00");
    }

    @PostMapping("/packages/{packageId}/click")
    public ResponseEntity<Event> recordPackageClick(
            @PathVariable Long packageId,
            HttpServletRequest request) {
        log.info("Registrando click en paquete: {}", packageId);

        var travelPackage = packageService.getPackageById(packageId);

        Event event = eventService.recordEvent(
                EventType.CLICK,
                travelPackage,
                null,
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @GetMapping("/packages/{packageId}/views")
    @PreAuthorize("hasRole('ASESOR') or hasRole('ADMIN')")
    public ResponseEntity<Long> getPackageViews(@PathVariable Long packageId) {
        log.info("Obteniendo vistas del paquete: {}", packageId);
        Long viewCount = eventService.getPackageViewCount(packageId);
        return ResponseEntity.ok(viewCount);
    }

    @GetMapping("/packages/{packageId}/metrics")
    @PreAuthorize("hasRole('ASESOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPackageMetrics(@PathVariable Long packageId) {
        log.info("Obteniendo métricas del paquete: {}", packageId);

        Long views = eventService.getPackageViewCount(packageId);
        Double conversionRate = eventService.calculateConversionRate(packageId);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("packageId", packageId);
        metrics.put("viewCount", views);
        metrics.put("conversionRate", conversionRate);

        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/packages/{packageId}/events")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Event>> getPackageEvents(
            @PathVariable Long packageId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("Obteniendo eventos del paquete: {}", packageId);

        List<Event> events;

        if (startDate != null && endDate != null) {
            events = eventService.getEventsByDateRange(
                    packageId,
                    parseQueryDateTime(startDate, "startDate"),
                    parseQueryDateTime(endDate, "endDate")
            );
        } else if (startDate != null || endDate != null) {
            throw new InvalidOperationException("Debe enviar startDate y endDate juntos, o ninguno");
        } else {
            events = eventService.getEventsByPackage(packageId);
        }

        return ResponseEntity.ok(events);
    }
}
