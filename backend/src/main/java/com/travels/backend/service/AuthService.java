package com.travels.backend.service;

import com.travels.backend.dto.AuthLoginDTO;
import com.travels.backend.dto.AuthResponseDTO;
import com.travels.backend.dto.UserRequestDTO;
import com.travels.backend.exception.InvalidOperationException;
import com.travels.backend.model.User;
import com.travels.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthResponseDTO register(UserRequestDTO dto) {
        log.info("Registrando nuevo usuario: {}", dto.getEmail());

        User user = userService.registerPublicUser(dto);

        String accessToken = tokenProvider.generateToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);

        return AuthResponseDTO.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(userService.convertToDTO(user))
                .build();
    }

    public AuthResponseDTO login(AuthLoginDTO dto) {
        log.info("Usuario intentando iniciar sesión: {}", dto.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );

            User user = (User) authentication.getPrincipal();

            String accessToken = tokenProvider.generateToken(user);
            String refreshToken = tokenProvider.generateRefreshToken(user);

            return AuthResponseDTO.builder()
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .user(userService.convertToDTO(user))
                    .build();

        } catch (Exception e) {
            throw new InvalidOperationException("Correo o contraseña incorrectos");
        }
    }

    public AuthResponseDTO refreshToken(String refreshToken) {
        log.info("Renovando token de acceso");

        String email = tokenProvider.extractUsername(refreshToken);
        User user = userService.getUserByEmail(email);

        if (!tokenProvider.isTokenValid(refreshToken, user)) {
            throw new InvalidOperationException("Token de refresco inválido o expirado");
        }

        String newAccessToken = tokenProvider.generateToken(user);

        return AuthResponseDTO.builder()
                .token(newAccessToken)
                .refreshToken(refreshToken)
                .user(userService.convertToDTO(user))
                .build();
    }
}
