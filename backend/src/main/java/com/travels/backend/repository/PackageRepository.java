package com.travels.backend.repository;

import com.travels.backend.model.Package;
import com.travels.backend.model.PackageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {
    List<Package> findByStatus(PackageStatus status);

    Page<Package> findByStatus(PackageStatus status, Pageable pageable);
    List<Package> findByDestination(String destination);

    @Query("SELECT p FROM Package p WHERE p.status = 'APPROVED' ORDER BY p.averageRating DESC LIMIT 10")
    List<Package> findTopRatedPackages();

    @Query("SELECT p FROM Package p WHERE p.status = 'APPROVED' ORDER BY p.createdAt DESC LIMIT 10")
    List<Package> findRecentPackages();
}
