package com.travels.backend.service;

import com.travels.backend.model.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarConfirmacionReserva(Booking booking) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(booking.getUser().getEmail());

        message.setSubject("Reserva confirmada");

        message.setText(
                "Hola " + booking.getUser().getName() +
                ", tu reserva fue confirmada correctamente."
        );

        mailSender.send(message);

        log.info("Correo enviado a {}", booking.getUser().getEmail());
    }
}