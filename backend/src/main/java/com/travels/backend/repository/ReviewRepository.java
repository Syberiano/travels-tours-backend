package com.travels.backend.repository;

import com.travels.backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTravelPackageId(Long packageId);
    List<Review> findByUserId(Long userId);
    Optional<Review> findByBookingId(Long bookingId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.travelPackage.id = ?1 AND r.verified = true")
    Double calculateAverageRating(Long packageId);

    @Query("SELECT r FROM Review r WHERE r.travelPackage.id = ?1 AND r.verified = true")
    List<Review> findVerifiedReviewsByPackageId(Long packageId);
}
