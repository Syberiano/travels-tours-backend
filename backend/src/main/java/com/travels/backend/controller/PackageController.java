package com.travels.backend.controller;

import com.travels.backend.dto.PackageDTO;
import com.travels.backend.dto.PackageRequestDTO;
import com.travels.backend.exception.ResourceNotFoundException;
import com.travels.backend.model.PackageStatus;
import com.travels.backend.model.UserRole;
import com.travels.backend.service.PackageService;
import com.travels.backend.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Paquetes turísticos", description = "CRUD de paquetes, aprobación (ADMIN) y listados públicos")
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
        // Solo devolver paquetes aprobados a usuarios no autenticados
        if (!travelPackage.getStatus().equals(PackageStatus.APPROVED)
                && !SecurityUtil.isCurrentUserStaff()) {
            throw new ResourceNotFoundException("Paquete no encontrado");
        }
        return ResponseEntity.ok(packageService.convertToDTO(travelPackage));
    }

    @GetMapping
    public ResponseEntity<List<PackageDTO>> getAllPackages() {
        log.info("Obteniendo todos los paquetes");
        var packages = packageService.getAllPackages();
        return ResponseEntity.ok(packageService.convertToDTO(packages));
    }

    @GetMapping("/approved")
    @Operation(summary = "Paquetes aprobados (paginado)", description = "Query params estándar de Spring: `page` (0-based), `size`, opcionalmente `sort`.")
    public ResponseEntity<Page<PackageDTO>> getApprovedPackages(Pageable pageable) {
        log.info("Obteniendo paquetes aprobados (page={}, size={})", pageable.getPageNumber(), pageable.getPageSize());
        var page = packageService.getApprovedPackages(pageable);
        return ResponseEntity.ok(page.map(packageService::convertToDTO));
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
    @PreAuthorize("hasRole('ASESOR') or hasRole('ADMIN')")
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
