package com.travels.backend.util;

import com.travels.backend.model.User;
import com.travels.backend.model.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return null;
        }
        return (User) authentication.getPrincipal();
    }

    public static boolean isCurrentUserAdmin() {
        User user = getCurrentUser();
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    public static boolean isCurrentUserAsesor() {
        User user = getCurrentUser();
        return user != null && user.getRole() == UserRole.ASESOR;
    }

    public static boolean isCurrentUserCliente() {
        User user = getCurrentUser();
        return user != null && user.getRole() == UserRole.CLIENTE;
    }
}