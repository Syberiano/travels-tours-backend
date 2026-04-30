package com.travels.backend.controller;

import com.travels.backend.dto.AuthLoginDTO;
import com.travels.backend.dto.AuthResponseDTO;
import com.travels.backend.dto.UserRequestDTO;
import com.travels.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody UserRequestDTO dto) {
        log.info("Registrando nuevo usuario: {}", dto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthLoginDTO dto) {
        log.info("Usuario intentando iniciar sesión: {}", dto.getEmail());
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestHeader("Authorization") String token) {
        String refreshToken = token.replace("Bearer ", "");
        log.info("Renovando token de acceso");
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
}
