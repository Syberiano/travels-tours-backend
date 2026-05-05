package com.travels.backend.controller;

import com.travels.backend.dto.AuthLoginDTO;
import com.travels.backend.dto.AuthResponseDTO;
import com.travels.backend.dto.UserRequestDTO;
import com.travels.backend.exception.InvalidOperationException;
import com.travels.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Autenticación", description = "Registro, login y renovación de tokens JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registro de cliente", description = "Crea usuario con rol CLIENTE y devuelve access + refresh token.")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody UserRequestDTO dto) {
        log.info("Registrando nuevo usuario: {}", dto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    @PostMapping("/login")
    @Operation(summary = "Inicio de sesión", description = "Devuelve JWT de acceso y refresh token.")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthLoginDTO dto) {
        log.info("Usuario intentando iniciar sesión: {}", dto.getEmail());
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/refresh-token")
    @Operation(
            summary = "Renovar access token",
            description = "Envía el refresh token en `Authorization: Bearer <refresh>` o en la cabecera `X-Refresh-Token`.")
    public ResponseEntity<AuthResponseDTO> refreshToken(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshHeader) {
        String refreshToken;
        if (refreshHeader != null && !refreshHeader.isBlank()) {
            refreshToken = refreshHeader.startsWith("Bearer ") ? refreshHeader.substring(7) : refreshHeader;
        } else if (authorization != null && authorization.startsWith("Bearer ")) {
            refreshToken = authorization.substring(7);
        } else {
            throw new InvalidOperationException(
                    "Envía el refresh token en Authorization: Bearer <token> o en la cabecera X-Refresh-Token");
        }
        log.info("Renovando token de acceso");
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
}
