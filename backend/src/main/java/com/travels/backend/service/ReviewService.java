package com.travels.backend.service;

import com.travels.backend.dto.ReviewDTO;
import com.travels.backend.dto.ReviewRequestDTO;
import com.travels.backend.exception.InvalidOperationException;
import com.travels.backend.exception.ResourceNotFoundException;
import com.travels.backend.model.*;
import com.travels.backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingService bookingService;
    private final PackageService packageService;
    private final UserService userService;

    public Review createReview(User user, ReviewRequestDTO dto) {
        log.info("Creando reseña de usuario: {} para reserva: {}", user.getId(), dto.getBookingId());

        Booking booking = bookingService.getBookingById(dto.getBookingId());

        // Validar que el usuario es el propietario de la reserva
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new InvalidOperationException("Solo el usuario que realizó la reserva puede dejar una reseña");
        }

        // Reseña permitida tras pago confirmado o al completar el viaje
        if (!booking.getStatus().equals(BookingStatus.COMPLETED)
                && !booking.getStatus().equals(BookingStatus.CONFIRMED)) {
            throw new InvalidOperationException("Solo se pueden reseñar reservas confirmadas o completadas");
        }

        // Validar que no existe reseña previa para esta reserva
        if (reviewRepository.findByBookingId(dto.getBookingId()).isPresent()) {
            throw new InvalidOperationException("Ya existe una reseña para esta reserva");
        }

        Review review = Review.builder()
                .user(user)
                .travelPackage(booking.getTravelPackage())
                .booking(booking)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .verified(true) // Automáticamente verificada por reserva completada
                .build();

        Review savedReview = reviewRepository.save(review);

        // Actualizar rating promedio del paquete
        packageService.updateAverageRating(booking.getTravelPackage().getId());

        return savedReview;
    }

    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada con ID: " + id));
    }

    public List<Review> getReviewsByPackage(Long packageId) {
        return reviewRepository.findVerifiedReviewsByPackageId(packageId);
    }

    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    public Review updateReview(Long id, ReviewRequestDTO dto) {
        log.info("Actualizando reseña con ID: {}", id);

        Review review = getReviewById(id);

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        Review updatedReview = reviewRepository.save(review);

        // Actualizar rating promedio del paquete
        packageService.updateAverageRating(review.getTravelPackage().getId());

        return updatedReview;
    }

    public void deleteReview(Long id) {
        log.info("Eliminando reseña con ID: {}", id);

        Review review = getReviewById(id);
        Long packageId = review.getTravelPackage().getId();

        reviewRepository.deleteById(id);
        reviewRepository.flush(); // Asegurar que la eliminación se procese antes del recálculo
        // Actualizar rating promedio del paquete
        packageService.updateAverageRating(packageId);
    }

    public Double getAverageRating(Long packageId) {
        return reviewRepository.calculateAverageRating(packageId);
    }

    public ReviewDTO convertToDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .user(userService.convertToDTO(review.getUser()))
                .travelPackage(packageService.convertToDTO(review.getTravelPackage()))
                .rating(review.getRating())
                .comment(review.getComment())
                .verified(review.getVerified())
                .build();
    }

    public List<ReviewDTO> convertToDTO(List<Review> reviews) {
        return reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
