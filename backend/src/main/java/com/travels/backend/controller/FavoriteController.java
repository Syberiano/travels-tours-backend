package com.travels.backend.controller;

import com.travels.backend.dto.FavoriteDTO;
import com.travels.backend.service.FavoriteService;
import com.travels.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Slf4j
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{packageId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<FavoriteDTO> addFavorite(@PathVariable Long packageId) {
        log.info("Agregando paquete {} a favoritos", packageId);
        var user = SecurityUtil.getCurrentUser();
        var favorite = favoriteService.addFavorite(user, packageId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favoriteService.convertToDTO(favorite));
    }

    @DeleteMapping("/{favoriteId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long favoriteId) {
        log.info("Removiendo favorito con ID: {}", favoriteId);
        favoriteService.removeFavorite(favoriteId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/package/{packageId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> removeFavoriteByPackage(@PathVariable Long packageId) {
        log.info("Removiendo paquete {} de favoritos", packageId);
        var user = SecurityUtil.getCurrentUser();
        favoriteService.removeFavorite(user, packageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-favorites")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<FavoriteDTO>> getMyFavorites() {
        log.info("Obteniendo mis favoritos");
        var user = SecurityUtil.getCurrentUser();
        var favorites = favoriteService.getFavoritesByUser(user.getId());
        return ResponseEntity.ok(favoriteService.convertToDTO(favorites));
    }

    @GetMapping("/{packageId}/is-favorite")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Boolean> isFavorite(@PathVariable Long packageId) {
        log.info("Verificando si el paquete {} está en favoritos", packageId);
        var user = SecurityUtil.getCurrentUser();
        Boolean isFavorite = favoriteService.isFavorite(user.getId(), packageId);
        return ResponseEntity.ok(isFavorite);
    }
}
