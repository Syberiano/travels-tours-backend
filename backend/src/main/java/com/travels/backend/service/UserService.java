package com.travels.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travels.backend.dto.UserDTO;
import com.travels.backend.dto.UserRequestDTO;
import com.travels.backend.exception.InvalidOperationException;
import com.travels.backend.exception.ResourceNotFoundException;
import com.travels.backend.model.User;
import com.travels.backend.model.UserRole;
import com.travels.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        if (!user.getActive()) {
            throw new UsernameNotFoundException("La cuenta de usuario está desactivada: " + email);
        }

        return user;
    }

    public User registerUser(UserRequestDTO dto) {
        log.info("Registrando nuevo usuario: {}", dto.getEmail());

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new InvalidOperationException("El correo ya está registrado");
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phone(dto.getPhone())
                .profileImage(dto.getProfileImage())
                .bio(dto.getBio())
                .role(dto.getRole() != null ? dto.getRole() : UserRole.CLIENTE)
                .active(true)
                .build();

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    public User updateUser(Long id, UserRequestDTO dto) {
        log.info("Actualizando usuario con ID: {}", id);

        User user = getUserById(id);

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getProfileImage() != null) {
            user.setProfileImage(dto.getProfileImage());
        }
        if (dto.getBio() != null) {
            user.setBio(dto.getBio());
        }
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        log.info("Eliminando usuario con ID: {}", id);

        User user = getUserById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    public UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .phone(user.getPhone())
                .profileImage(user.getProfileImage())
                .bio(user.getBio())
                .active(user.getActive())
                .build();
    }

    public List<UserDTO> convertToDTO(List<User> users) {
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public User changePassword(Long id, String oldPassword, String newPassword) {
        log.info("Cambiando contraseña del usuario con ID: {}", id);

        User user = getUserById(id);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidOperationException("La contraseña actual es incorrecta");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}
