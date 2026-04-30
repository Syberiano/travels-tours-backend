# Backend - Agencia de Viajes

Sistema backend completo para una plataforma de agencia de viajes con Spring Boot, arquitectura en capas y seguridad JWT.

## Características

✅ **Arquitectura en Capas**
- Controllers
- Services
- Repositories
- Models (DTOs)

✅ **Seguridad**
- Spring Security
- Autenticación JWT
- Control de acceso basado en roles (RBAC)
- Validación de permisos en endpoints

✅ **Entidades Implementadas**
- User (Usuario) - con roles ADMIN, ASESOR, CLIENTE
- Package (Paquete Turístico)
- Booking (Reservas)
- Review (Reseñas)
- Favorite (Favoritos)
- Blog (Contenido)
- Contact (Contacto)
- Event (Eventos y Analítica)

✅ **Funcionalidades**
- CRUD completo para todas las entidades
- DTOs para entrada y salida
- Validaciones con @Valid
- Manejo global de errores
- Gestión de estado de paquetes (PENDING, APPROVED, REJECTED)
- Gestión de estado de reservas con transiciones válidas
- Cálculo automático de ratings
- Analítica y seguimiento de eventos

## Requisitos

- Java 17+
- Spring Boot 4.0.6
- PostgreSQL 12+
- Maven

## Dependencias Principales

```xml
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-validation
- postgresql
- jjwt (JWT)
- lombok
```

## Configuración

### 1. Base de Datos

Crear la base de datos en PostgreSQL:

```sql
CREATE DATABASE travels_db;
```

### 2. Variables de Entorno

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/travels_db
spring.datasource.username=postgres
spring.datasource.password=your_password

jwt.secret=your_secret_key
jwt.expiration=86400000
jwt.refresh.expiration=604800000
```

### 3. Compilar y Ejecutar

```bash
# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run

# Ejecutar tests
mvn test
```

## Estructura del Proyecto

```
src/main/java/com/travels/backend/
├── controller/          # Controladores REST
├── service/            # Lógica de negocio
├── repository/         # Acceso a datos
├── model/             # Entidades JPA y DTOs
├── security/          # Configuración JWT
├── config/            # Configuración de la aplicación
├── exception/         # Manejo de excepciones
└── util/             # Utilidades
```

## Rol del Usuario y Permisos

### ADMIN
- ✓ Crear, editar, eliminar usuarios
- ✓ Aprobar/rechazar paquetes
- ✓ Aprobar/rechazar blogs
- ✓ Gestionar reservas
- ✓ Ver analítica
- ✓ Responder mensajes de contacto

### ASESOR
- ✓ Crear y editar paquetes (estado PENDING)
- ✓ Crear blogs (requieren aprobación)
- ✓ Ver métricas de paquetes
- ✗ Acceso a gestión de usuarios

### CLIENTE
- ✓ Registrarse y actualizar perfil
- ✓ Ver paquetes aprobados
- ✓ Realizar reservas
- ✓ Dejar reseñas (solo de reservas completadas)
- ✓ Agregar a favoritos
- ✓ Enviar mensajes de contacto
- ✗ Crear/editar paquetes
- ✗ Aprobar contenido

## Endpoints Principales

### Autenticación
```
POST   /api/auth/register           - Registrarse
POST   /api/auth/login              - Iniciar sesión
POST   /api/auth/refresh-token      - Renovar token
```

### Paquetes
```
GET    /api/packages                - Obtener todos
GET    /api/packages/approved       - Obtener aprobados
GET    /api/packages/{id}           - Obtener uno
POST   /api/packages                - Crear (ASESOR)
PUT    /api/packages/{id}           - Editar (ASESOR)
POST   /api/packages/{id}/approve   - Aprobar (ADMIN)
POST   /api/packages/{id}/reject    - Rechazar (ADMIN)
```

### Reservas
```
POST   /api/bookings                - Crear reserva
GET    /api/bookings/user/my-bookings - Mis reservas
PATCH  /api/bookings/{id}/status/{status} - Cambiar estado
```

### Reseñas
```
POST   /api/reviews                 - Crear reseña
GET    /api/reviews/package/{packageId} - Reseñas del paquete
GET    /api/reviews/user/my-reviews - Mis reseñas
```

### Favoritos
```
POST   /api/favorites/{packageId}   - Agregar favorito
GET    /api/favorites/my-favorites  - Mis favoritos
DELETE /api/favorites/{favoriteId}  - Eliminar favorito
```

### Blog
```
GET    /api/blogs/approved          - Blogs publicados
POST   /api/blogs                   - Crear blog (ASESOR)
POST   /api/blogs/{id}/approve      - Aprobar blog (ADMIN)
```

### Analítica
```
POST   /api/analytics/packages/{packageId}/click - Registrar click
GET    /api/analytics/packages/{packageId}/views - Obtener vistas
GET    /api/analytics/packages/{packageId}/metrics - Métricas
```

### Usuarios
```
GET    /api/users/profile           - Mi perfil
GET    /api/users                   - Todos (ADMIN)
GET    /api/users/{id}              - Un usuario (ADMIN)
PUT    /api/users/{id}              - Actualizar
DELETE /api/users/{id}              - Eliminar (ADMIN)
```

### Contacto
```
POST   /api/contacts                - Enviar mensaje (público)
GET    /api/contacts                - Obtener mensajes (ADMIN)
PATCH  /api/contacts/{id}/respond   - Responder mensaje (ADMIN)
```

## Reglas de Negocio Implementadas

### Paquetes
- ✓ Solo ASESOR puede crear/editar paquetes
- ✓ Nuevos paquetes inician en estado PENDING
- ✓ Solo ADMIN puede aprobar/rechazar
- ✓ Solo paquetes APPROVED son visibles para CLIENTE

### Reservas
- ✓ Solo CLIENTE puede reservar
- ✓ Solo paquetes APPROVED pueden ser reservados
- ✓ Transiciones de estado validadas (PENDING → CONFIRMED → IN_PROGRESS → COMPLETED)

### Reseñas
- ✓ Solo usuarios con reservas COMPLETED pueden reseñar
- ✓ Una reseña por reserva
- ✓ Reseñas verificadas automáticamente si son de reservas completadas
- ✓ Rating promedio calculado automáticamente

### Favoritos
- ✓ No se permiten duplicados
- ✓ No requiere haber comprado el paquete

### Blog
- ✓ Solo ASESOR/ADMIN pueden crear
- ✓ Requiere aprobación de ADMIN
- ✓ Solo aprobados son visibles

## Manejo de Errores

La aplicación implementa `@ControllerAdvice` para manejar excepciones globales:

```
- ResourceNotFoundException (404)
- InvalidOperationException (400)
- UnauthorizedException (401)
- AccessDeniedException (403)
- MethodArgumentNotValidException (400)
```

## Seguridad

### JWT Configuration
- Secret key configurable
- Token access: 24 horas por defecto
- Refresh token: 7 días por defecto
- Algoritmo: HS512

### Spring Security
- CORS configurado
- CSRF deshabilitado (API REST)
- Session Policy: STATELESS
- Filtro JWT personalizado

## Validaciones

- Email único por usuario
- Contraseña encriptada con BCrypt
- DTOs con validaciones (Email, NotBlank, Positive, etc.)
- Control de transiciones de estado

## Logging

Se implementó logging con SLF4J en todos los servicios y controladores para facilitar debugging y monitoreo.

## Próximas Mejoras

- [ ] Paginación en listados
- [ ] Búsqueda y filtros avanzados
- [ ] Envío de correos electrónicos
- [ ] Integración con proveedores OAuth
- [ ] Caché con Redis
- [ ] WebSockets para notificaciones en tiempo real
- [ ] Tests unitarios e integración
- [ ] Documentación Swagger/OpenAPI

## Autor

Sistema desarrollado con Spring Boot 4.0.6 y Java 17

