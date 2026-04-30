package com.travels.backend.repository;

import com.travels.backend.model.Booking;
import com.travels.backend.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByTravelPackageId(Long packageId);
    List<Booking> findByStatus(BookingStatus status);
    Long countByTravelPackageId(Long packageId);

    @Query("SELECT b FROM Booking b WHERE b.user.id = ?1 ORDER BY b.createdAt DESC")
    List<Booking> findUserBookingsHistory(Long userId);
}
