package com.travels.backend.controller;

import com.travels.backend.dto.ReviewDTO;
import com.travels.backend.dto.ReviewRequestDTO;
import com.travels.backend.service.ReviewService;
import com.travels.backend.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewRequestDTO dto) {
        log.info("Creando nueva reseña");
        var user = SecurityUtil.getCurrentUser();
        var review = reviewService.createReview(user, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.convertToDTO(review));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long id) {
        log.info("Obteniendo reseña con ID: {}", id);
        var review = reviewService.getReviewById(id);
        return ResponseEntity.ok(reviewService.convertToDTO(review));
    }

    @GetMapping("/package/{packageId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByPackage(@PathVariable Long packageId) {
        log.info("Obteniendo reseñas del paquete: {}", packageId);
        var reviews = reviewService.getReviewsByPackage(packageId);
        return ResponseEntity.ok(reviewService.convertToDTO(reviews));
    }

    @GetMapping("/user/my-reviews")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<ReviewDTO>> getMyReviews() {
        log.info("Obteniendo mis reseñas");
        var user = SecurityUtil.getCurrentUser();
        var reviews = reviewService.getReviewsByUser(user.getId());
        return ResponseEntity.ok(reviewService.convertToDTO(reviews));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDTO dto) {
        log.info("Actualizando reseña con ID: {}", id);
        var review = reviewService.updateReview(id, dto);
        return ResponseEntity.ok(reviewService.convertToDTO(review));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        log.info("Eliminando reseña con ID: {}", id);
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/package/{packageId}/rating")
    public ResponseEntity<Double> getPackageAverageRating(@PathVariable Long packageId) {
        log.info("Obteniendo calificación promedio del paquete: {}", packageId);
        Double avgRating = reviewService.getAverageRating(packageId);
        return ResponseEntity.ok(avgRating != null ? avgRating : 0.0);
    }
}
