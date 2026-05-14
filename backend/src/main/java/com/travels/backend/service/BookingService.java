package com.travels.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travels.backend.dto.BookingDTO;
import com.travels.backend.dto.BookingRequestDTO;
import com.travels.backend.exception.InvalidOperationException;
import com.travels.backend.exception.ResourceNotFoundException;
import com.travels.backend.model.Booking;
import com.travels.backend.model.BookingStatus;
import com.travels.backend.model.EventType;
import com.travels.backend.model.PackageStatus;
import com.travels.backend.model.User;
import com.travels.backend.model.UserRole;
import com.travels.backend.repository.BookingRepository;
import com.travels.backend.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PackageService packageService;
    private final UserService userService;
    private final EventService eventService;
    private final EmailService emailService;

    /**
     * Simula la confirmación de pago del cliente: solo el dueño puede confirmar una reserva PENDING.
     */
    public Booking confirmBookingPayment(User user, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Reserva no encontrada");
        }
        if (!booking.getStatus().equals(BookingStatus.PENDING)) {
            throw new InvalidOperationException("Solo se pueden confirmar pagos de reservas pendientes");
        }
        return updateBookingStatus(bookingId, BookingStatus.CONFIRMED);
    }

    public Booking createBooking(User user, BookingRequestDTO dto) {
        log.info("Creando reserva para usuario: {} y paquete: {}", user.getId(), dto.getPackageId());

        if (user.getRole() != UserRole.CLIENTE) {
            throw new InvalidOperationException("Solo los clientes pueden realizar reservas");
        }

        com.travels.backend.model.Package travelPackage = packageService.getPackageById(dto.getPackageId());

        if (!travelPackage.getStatus().equals(PackageStatus.APPROVED)) {
            throw new InvalidOperationException("Solo se pueden reservar paquetes aprobados");
        }

        Booking booking = Booking.builder()
                .user(user)
                .travelPackage(travelPackage)
                .status(BookingStatus.PENDING)
                .travelDate(dto.getTravelDate())
                .numberOfParticipants(dto.getNumberOfParticipants())
                .specialRequests(dto.getSpecialRequests())
                .totalPrice(travelPackage.getPrice() * dto.getNumberOfParticipants())
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        // Registrar evento de reserva para analítica (Tasa de conversión)
        eventService.recordEvent(EventType.BOOKING, travelPackage, user, null, "SYSTEM");

        return savedBooking;
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));
    }

    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findUserBookingsHistory(userId);
    }

    public List<Booking> getBookingsByPackage(Long packageId) {
        return bookingRepository.findByTravelPackageId(packageId);
    }

    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }

    /*
    public Booking updateBookingStatus(Long id, BookingStatus newStatus) {
        log.info("Actualizando estado de reserva con ID: {} a {}", id, newStatus);

        Booking booking = getBookingById(id);
        BookingStatus currentStatus = booking.getStatus();

        // Validar transiciones de estado válidas
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new InvalidOperationException(
                    "No se puede pasar de " + currentStatus + " a " + newStatus
            );
        }

        booking.setStatus(newStatus);

        if (newStatus.equals(BookingStatus.COMPLETED)) {
            booking.setCompletedAt(LocalDateTime.now());
        }

        return bookingRepository.save(booking);
    }
*/
    
    public Booking updateBookingStatus(Long id, BookingStatus newStatus) {
        log.info("Actualizando estado de reserva con ID: {} a {}", id, newStatus);

        Booking booking = getBookingById(id);
        BookingStatus currentStatus = booking.getStatus();

        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new InvalidOperationException(
                    "No se puede pasar de " + currentStatus + " a " + newStatus
            );
        }

        booking.setStatus(newStatus);

        if (newStatus.equals(BookingStatus.COMPLETED)) {
            booking.setCompletedAt(LocalDateTime.now());
        }

        Booking savedBooking = bookingRepository.save(booking);

        if (newStatus.equals(BookingStatus.CONFIRMED)) {
            try {
                emailService.enviarConfirmacionReserva(savedBooking);
            } catch (Exception e) {
                log.error("No se pudo enviar el correo de confirmación: {}", e.getMessage());
            }
        }

        return savedBooking;
    }
    
    private boolean isValidStatusTransition(BookingStatus from, BookingStatus to) {
        return switch (from) {
            case PENDING -> to.equals(BookingStatus.CONFIRMED) || to.equals(BookingStatus.CANCELLED);
            case CONFIRMED -> to.equals(BookingStatus.IN_PROGRESS) || to.equals(BookingStatus.CANCELLED);
            case IN_PROGRESS -> to.equals(BookingStatus.COMPLETED) || to.equals(BookingStatus.CANCELLED);
            case COMPLETED, CANCELLED -> false;
        };
    }

    public void deleteBooking(Long id) {
        log.info("Eliminando reserva con ID: {}", id);
        bookingRepository.deleteById(id);
    }

    public BookingDTO convertToDTO(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .user(userService.convertToDTO(booking.getUser()))
                .travelPackage(packageService.convertToDTO(booking.getTravelPackage()))
                .status(booking.getStatus())
                .travelDate(booking.getTravelDate())
                .totalPrice(booking.getTotalPrice())
                .numberOfParticipants(booking.getNumberOfParticipants())
                .specialRequests(booking.getSpecialRequests())
                .build();
    }

    public List<BookingDTO> convertToDTO(List<Booking> bookings) {
        return bookings.stream()
                .map(this::convertToDTO)
                .toList();
    }
}
