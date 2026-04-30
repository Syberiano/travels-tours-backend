# Colección de Endpoints - Backend Agencia de Viajes

## Base URL
`http://localhost:8080/api`

## Autenticación

Todos los endpoints protegidos requieren el siguiente header:
```
Authorization: Bearer {access_token}
```

---

## 1. AUTENTICACIÓN

### Registrar Usuario
```
POST /auth/register
Content-Type: application/json

{
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "password": "password123",
  "role": "CLIENTE",
  "phone": "+34 600 123 456",
  "bio": "Amante de los viajes"
}

Response: 201 Created
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "user": {
    "id": 1,
    "name": "Juan Pérez",
    "email": "juan@example.com",
    "role": "CLIENTE"
  }
}
```

### Iniciar Sesión
```
POST /auth/login
Content-Type: application/json

{
  "email": "juan@example.com",
  "password": "password123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "user": {...}
}
```

### Renovar Token
```
POST /auth/refresh-token
Authorization: Bearer {refreshToken}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "user": {...}
}
```

---

## 2. PAQUETES TURÍSTICOS

### Crear Paquete (ASESOR)
```
POST /packages
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "name": "Viaje a Barcelona",
  "description": "Disfruta 5 días en la capital catalana",
  "price": 1200.50,
  "destination": "Barcelona, España",
  "itinerary": "Día 1: Llegada... Día 5: Retorno",
  "images": "url1,url2,url3"
}

Response: 201 Created
{
  "id": 1,
  "name": "Viaje a Barcelona",
  "status": "PENDING",
  "averageRating": 0.0
}
```

### Obtener Paquetes Aprobados (Público)
```
GET /packages/approved

Response: 200 OK
[
  {
    "id": 1,
    "name": "París: Romance y Cultura",
    "price": 1500.00,
    "destination": "París, Francia",
    "status": "APPROVED",
    "averageRating": 4.8,
    "viewCount": 250,
    "bookingCount": 15
  }
]
```

### Obtener Paquete por ID (Público)
```
GET /packages/{id}

Response: 200 OK
{
  "id": 1,
  "name": "París: Romance y Cultura",
  "description": "Disfruta 7 días en la ciudad del amor...",
  "price": 1500.00,
  "destination": "París, Francia",
  "itinerary": "Día 1: Llegada... Día 7: Retorno",
  "status": "APPROVED",
  "averageRating": 4.8
}
```

### Aprobar Paquete (ADMIN)
```
POST /packages/{id}/approve
Authorization: Bearer {admin_token}

Response: 200 OK
{
  "id": 1,
  "status": "APPROVED",
  "approvedAt": "2024-04-29T10:30:00"
}
```

### Rechazar Paquete (ADMIN)
```
POST /packages/{id}/reject
Authorization: Bearer {admin_token}

Response: 200 OK
{
  "id": 1,
  "status": "REJECTED"
}
```

---

## 3. RESERVAS

### Crear Reserva (CLIENTE)
```
POST /bookings
Authorization: Bearer {client_token}
Content-Type: application/json

{
  "packageId": 1,
  "numberOfParticipants": 2,
  "specialRequests": "Habitación con vista al mar"
}

Response: 201 Created
{
  "id": 1,
  "status": "PENDING",
  "totalPrice": 3000.00,
  "numberOfParticipants": 2
}
```

### Obtener Mis Reservas (CLIENTE)
```
GET /bookings/user/my-bookings
Authorization: Bearer {client_token}

Response: 200 OK
[
  {
    "id": 1,
    "travelPackage": {...},
    "status": "PENDING",
    "totalPrice": 3000.00,
    "numberOfParticipants": 2
  }
]
```

### Cambiar Estado de Reserva (ADMIN)
```
PATCH /bookings/{id}/status/CONFIRMED
Authorization: Bearer {admin_token}

Response: 200 OK
{
  "id": 1,
  "status": "CONFIRMED"
}

Estados válidos: PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED
Transiciones válidas:
- PENDING → CONFIRMED o CANCELLED
- CONFIRMED → IN_PROGRESS o CANCELLED
- IN_PROGRESS → COMPLETED o CANCELLED
```

---

## 4. RESEÑAS

### Crear Reseña (CLIENTE - solo si reserva está COMPLETED)
```
POST /reviews
Authorization: Bearer {client_token}
Content-Type: application/json

{
  "bookingId": 1,
  "rating": 5,
  "comment": "Experiencia increíble, totalmente recomendado"
}

Response: 201 Created
{
  "id": 1,
  "rating": 5,
  "comment": "Experiencia increíble...",
  "verified": true
}
```

### Obtener Reseñas del Paquete (Público)
```
GET /reviews/package/{packageId}

Response: 200 OK
[
  {
    "id": 1,
    "user": {...},
    "rating": 5,
    "comment": "Experiencia increíble...",
    "verified": true
  }
]
```

### Obtener Calificación Promedio (Público)
```
GET /reviews/package/{packageId}/rating

Response: 200 OK
4.8
```

---

## 5. FAVORITOS

### Agregar a Favoritos (CLIENTE)
```
POST /favorites/{packageId}
Authorization: Bearer {client_token}

Response: 201 Created
{
  "id": 1,
  "travelPackage": {...}
}
```

### Obtener Mis Favoritos (CLIENTE)
```
GET /favorites/my-favorites
Authorization: Bearer {client_token}

Response: 200 OK
[
  {
    "id": 1,
    "travelPackage": {...}
  }
]
```

### Verificar si está en Favoritos (CLIENTE)
```
GET /favorites/{packageId}/is-favorite
Authorization: Bearer {client_token}

Response: 200 OK
true
```

### Eliminar de Favoritos (CLIENTE)
```
DELETE /favorites/{packageId}
Authorization: Bearer {client_token}

Response: 204 No Content
```

---

## 6. BLOG

### Crear Blog (ASESOR)
```
POST /blogs
Authorization: Bearer {asesor_token}
Content-Type: application/json

{
  "title": "Los mejores destinos de verano",
  "content": "Este año te recomendamos...",
  "featuredImage": "https://example.com/image.jpg"
}

Response: 201 Created
{
  "id": 1,
  "title": "Los mejores destinos de verano",
  "status": "PENDING"
}
```

### Obtener Blogs Aprobados (Público)
```
GET /blogs/approved

Response: 200 OK
[
  {
    "id": 1,
    "title": "Los mejores destinos de 2024",
    "author": {...},
    "status": "APPROVED",
    "viewCount": 150
  }
]
```

### Aprobar Blog (ADMIN)
```
POST /blogs/{id}/approve
Authorization: Bearer {admin_token}

Response: 200 OK
{
  "id": 1,
  "status": "APPROVED"
}
```

---

## 7. CONTACTO

### Enviar Mensaje de Contacto (Público)
```
POST /contacts
Content-Type: application/json

{
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "phone": "+34 600 123 456",
  "message": "Me gustaría información sobre viajes a Asia"
}

Response: 201 Created
{
  "id": 1,
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "resolved": false
}
```

### Obtener Mensajes (ADMIN)
```
GET /contacts
Authorization: Bearer {admin_token}

Response: 200 OK
[{...}]
```

### Obtener Mensajes No Resueltos (ADMIN)
```
GET /contacts/unresolved
Authorization: Bearer {admin_token}

Response: 200 OK
[{...}]
```

### Responder Mensaje (ADMIN)
```
PATCH /contacts/{id}/respond
Authorization: Bearer {admin_token}
Content-Type: application/json

"Gracias por su interés. Le enviamos información sobre nuestros paquetes de Asia."

Response: 200 OK
{
  "id": 1,
  "resolved": true,
  "response": "Gracias por su interés..."
}
```

---

## 8. ANALÍTICA

### Registrar Click en Paquete (Público)
```
POST /analytics/packages/{packageId}/click

Response: 201 Created
{
  "id": 1,
  "type": "CLICK",
  "createdAt": "2024-04-29T10:30:00"
}
```

### Obtener Vistas del Paquete (ASESOR/ADMIN)
```
GET /analytics/packages/{packageId}/views
Authorization: Bearer {asesor_token}

Response: 200 OK
250
```

### Obtener Métricas del Paquete (ASESOR/ADMIN)
```
GET /analytics/packages/{packageId}/metrics
Authorization: Bearer {asesor_token}

Response: 200 OK
{
  "packageId": 1,
  "viewCount": 250,
  "conversionRate": 6.0
}
```

---

## 9. USUARIOS

### Obtener Mi Perfil (Autenticado)
```
GET /users/profile
Authorization: Bearer {access_token}

Response: 200 OK
{
  "id": 1,
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "role": "CLIENTE"
}
```

### Actualizar Perfil
```
PUT /users/{id}
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "name": "Juan Carlos Pérez",
  "phone": "+34 600 999 888",
  "bio": "Viajero profesional"
}

Response: 200 OK
{...}
```

### Cambiar Contraseña
```
POST /users/{id}/change-password?oldPassword=old123&newPassword=new456
Authorization: Bearer {access_token}

Response: 200 OK
```

### Obtener Todos los Usuarios (ADMIN)
```
GET /users
Authorization: Bearer {admin_token}

Response: 200 OK
[{...}]
```

### Obtener Usuarios por Rol (ADMIN)
```
GET /users/role/CLIENTE
Authorization: Bearer {admin_token}

Response: 200 OK
[{...}]
```

---

## Códigos de Error

```
200 OK              - Solicitud exitosa
201 Created         - Recurso creado
204 No Content      - Solicitud exitosa sin contenido
400 Bad Request     - Error de validación
401 Unauthorized    - Token inválido o no autenticado
403 Forbidden       - Acceso denegado (sin permisos)
404 Not Found       - Recurso no encontrado
500 Internal Error  - Error del servidor
```

## Respuesta de Error Estándar

```json
{
  "timestamp": "2024-04-29T10:30:00",
  "status": 400,
  "error": "Error de validación",
  "message": "El nombre es obligatorio; El correo debe ser válido;",
  "path": "/api/users"
}
```

---

## Credenciales de Prueba

```
Admin:
  Email: admin@travels.com
  Password: password123
  Role: ADMIN

Asesor:
  Email: asesor@travels.com
  Password: password123
  Role: ASESOR

Cliente:
  Email: cliente@travels.com
  Password: password123
  Role: CLIENTE
```

---

**Última actualización: 2024-04-29**
