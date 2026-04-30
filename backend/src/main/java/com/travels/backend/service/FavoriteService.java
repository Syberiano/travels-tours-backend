package com.travels.backend.service;

import com.travels.backend.dto.FavoriteDTO;
import com.travels.backend.exception.InvalidOperationException;
import com.travels.backend.exception.ResourceNotFoundException;
import com.travels.backend.model.Favorite;
import com.travels.backend.model.User;
import com.travels.backend.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final PackageService packageService;
    private final UserService userService;

    public Favorite addFavorite(User user, Long packageId) {
        log.info("Agregando paquete {} a favoritos del usuario {}", packageId, user.getId());

        // Validar que el paquete existe
        var travelPackage = packageService.getPackageById(packageId);

        // Validar que no esté duplicado
        if (favoriteRepository.findByUserIdAndTravelPackageId(user.getId(), packageId).isPresent()) {
            throw new InvalidOperationException("Este paquete ya está en favoritos");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .travelPackage(travelPackage)
                .build();

        return favoriteRepository.save(favorite);
    }

    public void removeFavorite(Long favoriteId) {
        log.info("Eliminando favorito con ID: {}", favoriteId);

        Favorite favorite = getFavoriteById(favoriteId);
        favoriteRepository.delete(favorite);
    }

    public void removeFavorite(User user, Long packageId) {
        log.info("Removiendo paquete {} de favoritos del usuario {}", packageId, user.getId());

        Favorite favorite = favoriteRepository.findByUserIdAndTravelPackageId(user.getId(), packageId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorito no encontrado"));

        favoriteRepository.delete(favorite);
    }

    public Favorite getFavoriteById(Long id) {
        return favoriteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Favorito no encontrado con ID: " + id));
    }

    public List<Favorite> getFavoritesByUser(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    public Boolean isFavorite(Long userId, Long packageId) {
        return favoriteRepository.findByUserIdAndTravelPackageId(userId, packageId).isPresent();
    }

    public FavoriteDTO convertToDTO(Favorite favorite) {
        return FavoriteDTO.builder()
                .id(favorite.getId())
                .user(userService.convertToDTO(favorite.getUser()))
                .travelPackage(packageService.convertToDTO(favorite.getTravelPackage()))
                .build();
    }

    public List<FavoriteDTO> convertToDTO(List<Favorite> favorites) {
        return favorites.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
