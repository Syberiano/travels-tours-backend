package com.travels.backend.repository;

import com.travels.backend.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    Optional<Favorite> findByUserIdAndTravelPackageId(Long userId, Long packageId);
    Long countByTravelPackageId(Long packageId);
}
