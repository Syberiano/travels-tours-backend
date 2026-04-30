package com.travels.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travels.backend.dto.UserDTO;
import com.travels.backend.dto.UserRequestDTO;
import com.travels.backend.model.UserRole;
import com.travels.backend.service.UserService;
import com.travels.backend.util.SecurityUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getCurrentUserProfile() {
        log.info("Obteniendo perfil del usuario actual");
        var user = SecurityUtil.getCurrentUser();
        return ResponseEntity.ok(userService.convertToDTO(user));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        log.info("Admin creando nuevo usuario con email: {}", dto.getEmail());
        var user = userService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.convertToDTO(user));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        log.info("Obteniendo usuario con ID: {}", id);
        var user = userService.getUserById(id);
        return ResponseEntity.ok(userService.convertToDTO(user));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("Obteniendo todos los usuarios");
        var users = userService.getAllUsers();
        return ResponseEntity.ok(userService.convertToDTO(users));
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable UserRole role) {
        log.info("Obteniendo usuarios con rol: {}", role);
        var users = userService.getUsersByRole(role);
        return ResponseEntity.ok(userService.convertToDTO(users));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #id")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO dto) {
        log.info("Actualizando usuario con ID: {}", id);
        
        // Seguridad: Solo un ADMIN puede cambiar el rol de un usuario
        if (!SecurityUtil.getCurrentUser().getRole().equals(UserRole.ADMIN)) {
            dto.setRole(null);
        }
        
        var user = userService.updateUser(id, dto);
        return ResponseEntity.ok(userService.convertToDTO(user));
    }

    @PostMapping("/{id}/change-password")
    @PreAuthorize("authentication.principal.id == #id")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        log.info("Cambiando contraseña del usuario con ID: {}", id);
        userService.changePassword(id, oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Eliminando usuario con ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
