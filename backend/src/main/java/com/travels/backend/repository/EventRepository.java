package com.travels.backend.repository;

import com.travels.backend.model.Event;
import com.travels.backend.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByTravelPackageId(Long packageId);
    List<Event> findByUserId(Long userId);
    List<Event> findByType(EventType type);
    Long countByTravelPackageIdAndType(Long packageId, EventType type);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.travelPackage.id = ?1 AND e.type = 'CLICK'")
    Long countViewsByPackageId(Long packageId);

    @Query("SELECT e FROM Event e WHERE e.travelPackage.id = ?1 AND e.createdAt BETWEEN ?2 AND ?3")
    List<Event> findEventsByPackageAndDateRange(Long packageId, LocalDateTime startDate, LocalDateTime endDate);
}
