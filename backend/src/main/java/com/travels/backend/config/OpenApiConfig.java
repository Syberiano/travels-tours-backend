package com.travels.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearer-jwt";

    @Bean
    public OpenAPI travelsOpenApi(
            @Value("${spring.application.name:Travels Tours API}") String title,
            @Value("${api.server.url:http://localhost:8080}") String serverUrl) {
        return new OpenAPI()
                .servers(List.of(new Server().url(serverUrl).description("Servidor actual")))
                .info(new Info()
                        .title(title)
                        .description("""
                                API REST para la aplicación Travels & Tours: paquetes, reservas, blogs, \
                                reseñas, favoritos, contacto y analítica. \
                                Autenticación con JWT (Bearer). Los endpoints públicos no requieren token.""")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Travels Tours")
                                .email("contacto@travels.local"))
                        .license(new License().name("Uso interno / académico").url("https://opensource.org/licenses/MIT")))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME,
                                new SecurityScheme()
                                        .name(BEARER_SCHEME)
                                        .description("JWT devuelto por `/api/auth/login` o `/api/auth/register`. Prefijo opcional al pegar el valor.")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
        // No se añade security global: hay rutas públicas; usa "Authorize" en Swagger para enviar el JWT.
    }
}
