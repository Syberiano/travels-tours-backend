package com.travels.backend.service;

import com.travels.backend.dto.PackageDTO;
import com.travels.backend.dto.PackageRequestDTO;
import com.travels.backend.exception.InvalidOperationException;
import com.travels.backend.exception.ResourceNotFoundException;
import com.travels.backend.model.Package;
import com.travels.backend.model.PackageStatus;
import com.travels.backend.repository.PackageRepository;
import com.travels.backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PackageService {

    private final PackageRepository packageRepository;
    private final ReviewRepository reviewRepository;

    public Package createPackage(PackageRequestDTO dto) {
        log.info("Creando nuevo paquete en estado PENDING: {}", dto.getName());
        Package travelPackage = Package.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .destination(dto.getDestination())
                .itinerary(dto.getItinerary())
                .images(dto.getImages())
                .status(PackageStatus.PENDING) // Regla de negocio: inicia siempre en PENDING
                .averageRating(0.0)
                .build();
        return packageRepository.save(travelPackage);
    }

    public Package getPackageById(Long id) {
        return packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paquete no encontrado con ID: " + id));
    }

    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    public List<Package> getApprovedPackages() {
        return packageRepository.findByStatus(PackageStatus.APPROVED);
    }

    public List<Package> getRecentPackages() {
        return packageRepository.findRecentPackages();
    }

    public List<Package> getTopRatedPackages() {
        return packageRepository.findTopRatedPackages();
    }

    public List<Package> getPackagesByDestination(String destination) {
        return packageRepository.findByDestination(destination);
    }

    public Package updatePackage(Long id, PackageRequestDTO dto) {
        Package travelPackage = getPackageById(id);
        travelPackage.setName(dto.getName());
        travelPackage.setDescription(dto.getDescription());
        travelPackage.setPrice(dto.getPrice());
        travelPackage.setDestination(dto.getDestination());
        travelPackage.setItinerary(dto.getItinerary());
        travelPackage.setImages(dto.getImages());
        return packageRepository.save(travelPackage);
    }

    public Package approvePackage(Long id) {
        Package travelPackage = getPackageById(id);
        if (travelPackage.getStatus() != PackageStatus.PENDING) {
            throw new InvalidOperationException("Solo se pueden aprobar paquetes en estado PENDING");
        }
        travelPackage.setStatus(PackageStatus.APPROVED);
        travelPackage.setApprovedAt(LocalDateTime.now());
        return packageRepository.save(travelPackage);
    }

    public Package rejectPackage(Long id) {
        Package travelPackage = getPackageById(id);
        travelPackage.setStatus(PackageStatus.REJECTED);
        return packageRepository.save(travelPackage);
    }

    public void deletePackage(Long id) {
        if (!packageRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Paquete no encontrado");
        }
        packageRepository.deleteById(id);
    }

    public void updateAverageRating(Long packageId) {
        log.info("Actualizando rating promedio para el paquete: {}", packageId);
        Package travelPackage = getPackageById(packageId);
        Double average = reviewRepository.calculateAverageRating(packageId);
        travelPackage.setAverageRating(average != null ? average : 0.0);
        packageRepository.save(travelPackage);
    }

    public PackageDTO convertToDTO(Package travelPackage) {
        if (travelPackage == null) return null;
        return PackageDTO.builder()
                .id(travelPackage.getId())
                .name(travelPackage.getName())
                .description(travelPackage.getDescription())
                .price(travelPackage.getPrice())
                .destination(travelPackage.getDestination())
                .itinerary(travelPackage.getItinerary())
                .status(travelPackage.getStatus())
                .averageRating(travelPackage.getAverageRating())
                .build();
    }

    public List<PackageDTO> convertToDTO(List<Package> packages) {
        return packages.stream()
                .map(this::convertToDTO)
                .toList();
    }
}
