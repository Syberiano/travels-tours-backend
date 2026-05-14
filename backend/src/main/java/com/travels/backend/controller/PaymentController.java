package com.travels.backend.controller;

import com.travels.backend.dto.BookingDTO;
import com.travels.backend.dto.PaymentConfirmRequestDTO;
import com.travels.backend.service.BookingService;
import com.travels.backend.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Pagos", description = "Simulación de pasarela de pago")
public class PaymentController {

    private final BookingService bookingService;

    @PostMapping(value = "/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(
            summary = "Confirmar pago (simulado)",
            description = "Cabecera `Content-Type: application/json` obligatoria. Cuerpo: `{\"bookingId\": <id>}`. "
                    + "Solo reservas PENDING del cliente autenticado pasan a CONFIRMED.")
    public ResponseEntity<BookingDTO> confirmPayment(@Valid @RequestBody PaymentConfirmRequestDTO dto) {
        log.info("Confirmación de pago simulada para reserva: {}", dto.getBookingId());
        var user = SecurityUtil.getCurrentUser();
        var booking = bookingService.confirmBookingPayment(user, dto.getBookingId());
        return ResponseEntity.ok(bookingService.convertToDTO(booking));
    }
}
