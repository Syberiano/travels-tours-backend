package com.travels.backend.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Beans.
 * Se eliminan las definiciones manuales con 'null' para permitir que 
 * Spring Boot use Autowiring mediante las anotaciones @Service.
 */
@Configuration
public class BeansConfig {
}
