package com.travels.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación principal para el Backend de la Agencia de Viajes
 * 
 * Arquitectura:
 * - Controllers: Manejan las solicitudes HTTP
 * - Services: Contienen la lógica de negocio
 * - Repositories: Acceso a la base de datos
 * - Models: Entidades y DTOs
 * 
 * Seguridad:
 * - Spring Security + JWT
 * - Roles: ADMIN, ASESOR, CLIENTE
 * - Endpoints protegidos según permisos
 * 
 * Base de Datos:
 * - PostgreSQL
 * - JPA/Hibernate
 * - Relaciones: One-to-Many, Many-to-One, One-to-One
 * 
 * @author Sistema de Viajes
 * @version 1.0.0
 */
@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}

