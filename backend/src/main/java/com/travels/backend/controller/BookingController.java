package com.travels.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travels.backend.dto.BookingDTO;
import com.travels.backend.dto.BookingRequestDTO;
import com.travels.backend.exception.ResourceNotFoundException;
import com.travels.backend.model.BookingStatus;
import com.travels.backend.model.UserRole;
import com.travels.backend.service.BookingService;
import com.travels.backend.util.SecurityUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reservas", description = "Reservas de clientes y gestión por asesores/admin")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<BookingDTO> createBooking(
            @Valid @RequestBody BookingRequestDTO dto,
            HttpServletRequest request) {
        var user = SecurityUtil.getCurrentUser();
        var booking = bookingService.createBooking(user, dto); // El servicio ya registra el evento analítico
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.convertToDTO(booking));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id) {
        log.info("Obteniendo reserva con ID: {}", id);
        var booking = bookingService.getBookingById(id);
        var current = SecurityUtil.getCurrentUser();
        if (current.getRole() == UserRole.CLIENTE
                && !booking.getUser().getId().equals(current.getId())) {
            throw new ResourceNotFoundException("Reserva no encontrada");
        }
        return ResponseEntity.ok(bookingService.convertToDTO(booking));
    }

    @GetMapping("/user/my-bookings")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<BookingDTO>> getMyBookings() {
        log.info("Obteniendo mis reservas");
        var user = SecurityUtil.getCurrentUser();
        var bookings = bookingService.getBookingsByUser(user.getId());
        return ResponseEntity.ok(bookingService.convertToDTO(bookings));
    }

    @GetMapping("/package/{packageId}")
    @PreAuthorize("hasRole('ASESOR') or hasRole('ADMIN')")
    public ResponseEntity<List<BookingDTO>> getBookingsByPackage(@PathVariable Long packageId) {
        log.info("Obteniendo reservas del paquete: {}", packageId);
        var bookings = bookingService.getBookingsByPackage(packageId);
        return ResponseEntity.ok(bookingService.convertToDTO(bookings));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingDTO>> getBookingsByStatus(@PathVariable BookingStatus status) {
        log.info("Obteniendo reservas con estado: {}", status);
        var bookings = bookingService.getBookingsByStatus(status);
        return ResponseEntity.ok(bookingService.convertToDTO(bookings));
    }

    @PatchMapping("/{id}/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingDTO> updateBookingStatus(
            @PathVariable Long id,
            @PathVariable BookingStatus status) {
        log.info("Actualizando estado de reserva {} a {}", id, status);
        var booking = bookingService.updateBookingStatus(id, status);
        return ResponseEntity.ok(bookingService.convertToDTO(booking));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        log.info("Eliminando reserva con ID: {}", id);
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
