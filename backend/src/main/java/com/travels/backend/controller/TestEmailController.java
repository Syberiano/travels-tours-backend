package com.travels.backend.controller;

import com.travels.backend.model.Booking;
import com.travels.backend.service.BookingService;
import com.travels.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@Slf4j
public class TestEmailController {

    private final BookingService bookingService;
    private final EmailService emailService;

    @GetMapping("/test-email/{id}")
    public String testEmail(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);
        emailService.enviarConfirmacionReserva(booking);
        return "Correo enviado";
    }
}