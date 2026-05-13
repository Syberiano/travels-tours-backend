package com.travels.backend.service;

import com.travels.backend.model.Booking;
import com.travels.backend.model.BookingStatus;
import com.travels.backend.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final BookingRepository bookingRepository;
    //@Scheduled(fixedRate = 10000) ESTO ES PARA HACER QUE EL SERVICIO SE ACTUALICE CADA 10 SEGS Y NO CADA HORA
    @Scheduled(cron = "0 0 * * * *") //Este actualiza cada hora
    @Transactional
    public void actualizarReservas() {
        LocalDate hoy = LocalDate.now();
        int actualizadas = 0;

        List<Booking> confirmedBookings = bookingRepository.findByStatus(BookingStatus.CONFIRMED);
        for (Booking booking : confirmedBookings) {
            if (!booking.getTravelDate().isAfter(hoy)) {
                booking.setStatus(BookingStatus.IN_PROGRESS);
                actualizadas++;
            }
        }

        List<Booking> inProgressBookings = bookingRepository.findByStatus(BookingStatus.IN_PROGRESS);
        for (Booking booking : inProgressBookings) {
            LocalDate fechaFinViaje = booking.getTravelDate()
                    .plusDays(booking.getTravelPackage().getDurationDays());

            if (!hoy.isBefore(fechaFinViaje)) {
                booking.setStatus(BookingStatus.COMPLETED);
                booking.setCompletedAt(LocalDateTime.now());
                actualizadas++;
            }
        }

        if (actualizadas > 0) {
            bookingRepository.saveAll(confirmedBookings);
            bookingRepository.saveAll(inProgressBookings);
        }

        log.info("Scheduler ejecutado. Reservas actualizadas: {}", actualizadas);
    }
}