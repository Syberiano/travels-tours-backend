package com.travels.backend.controller;

import com.travels.backend.dto.PackageDTO;
import com.travels.backend.dto.PackageRequestDTO;
import com.travels.backend.model.UserRole;
import com.travels.backend.service.PackageService;
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
@RequestMapping("/api/packages")
@RequiredArgsConstructor
@Slf4j
public class PackageController {

    private final PackageService packageService;

    @PostMapping
    @PreAuthorize("hasRole('ASESOR')")
    public ResponseEntity<PackageDTO> createPackage(@Valid @RequestBody PackageRequestDTO dto) {
        log.info("Creando nuevo paquete: {}", dto.getName());
        var travelPackage = packageService.createPackage(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(packageService.convertToDTO(travelPackage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PackageDTO> getPackageById(@PathVariable Long id) {
        log.info("Obteniendo paquete con ID: {}", id);
        var travelPackage = packageService.getPackageById(id);
        return ResponseEntity.ok(packageService.convertToDTO(travelPackage));
    }

    @GetMapping
    public ResponseEntity<List<PackageDTO>> getAllPackages() {
        log.info("Obteniendo todos los paquetes");
        var packages = packageService.getAllPackages();
        return ResponseEntity.ok(packageService.convertToDTO(packages));
    }

    @GetMapping("/approved")
    public ResponseEntity<List<PackageDTO>> getApprovedPackages() {
        log.info("Obteniendo paquetes aprobados");
        var packages = packageService.getApprovedPackages();
        return ResponseEntity.ok(packageService.convertToDTO(packages));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<PackageDTO>> getRecentPackages() {
        log.info("Obteniendo paquetes recientes");
        var packages = packageService.getRecentPackages();
        return ResponseEntity.ok(packageService.convertToDTO(packages));
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<PackageDTO>> getTopRatedPackages() {
        log.info("Obteniendo paquetes mejor calificados");
        var packages = packageService.getTopRatedPackages();
        return ResponseEntity.ok(packageService.convertToDTO(packages));
    }

    @GetMapping("/destination/{destination}")
    public ResponseEntity<List<PackageDTO>> getPackagesByDestination(@PathVariable String destination) {
        log.info("Obteniendo paquetes por destino: {}", destination);
        var packages = packageService.getPackagesByDestination(destination);
        return ResponseEntity.ok(packageService.convertToDTO(packages));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ASESOR')")
    public ResponseEntity<PackageDTO> updatePackage(
            @PathVariable Long id,
            @Valid @RequestBody PackageRequestDTO dto) {
        log.info("Actualizando paquete con ID: {}", id);
        var travelPackage = packageService.updatePackage(id, dto);
        return ResponseEntity.ok(packageService.convertToDTO(travelPackage));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PackageDTO> approvePackage(@PathVariable Long id) {
        log.info("Aprobando paquete con ID: {}", id);
        var travelPackage = packageService.approvePackage(id);
        return ResponseEntity.ok(packageService.convertToDTO(travelPackage));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PackageDTO> rejectPackage(@PathVariable Long id) {
        log.info("Rechazando paquete con ID: {}", id);
        var travelPackage = packageService.rejectPackage(id);
        return ResponseEntity.ok(packageService.convertToDTO(travelPackage));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        log.info("Eliminando paquete con ID: {}", id);
        packageService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }
}
