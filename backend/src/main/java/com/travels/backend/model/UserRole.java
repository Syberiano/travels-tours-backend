package com.travels.backend.model;

public enum UserRole {
    ADMIN("admin"),
    ASESOR("asesor"),
    CLIENTE("cliente");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
