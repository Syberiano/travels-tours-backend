-- Script de inicialización de datos para Agencia de Viajes

-- Insertar usuarios de prueba
INSERT INTO users (name, email, password, role, created_at, updated_at, active) VALUES
('Admin Sistema', 'admin@travels.com', '$2a$10$slYQmyNdGzin7olVZiYM.OPST9/PgBkqquzi.Ss4lvYoMxJQnH63.', 'ADMIN', NOW(), NOW(), true),
('Asesor Premium', 'asesor@travels.com', '$2a$10$slYQmyNdGzin7olVZiYM.OPST9/PgBkqquzi.Ss4lvYoMxJQnH63.', 'ASESOR', NOW(), NOW(), true),
('Cliente VIP', 'cliente@travels.com', '$2a$10$slYQmyNdGzin7olVZiYM.OPST9/PgBkqquzi.Ss4lvYoMxJQnH63.', 'CLIENTE', NOW(), NOW(), true);

-- Insertar paquetes turísticos
INSERT INTO packages (name, description, price, destination, itinerary, status, average_rating, created_at, updated_at, approved_at) VALUES
('París: Romance y Cultura', 'Disfruta 7 días en la ciudad del amor con visitas a la Torre Eiffel, Louvre y Versalles', 1500.00, 'París, Francia', 'Día 1: Llegada e instalación. Día 2: Torre Eiffel y Crucero Sena. Día 3-5: Museos y Palacios...', 'APPROVED', 4.8, NOW(), NOW(), NOW()),
('Cancún: Playas Paradisíacas', 'Relájate 5 días en las mejores playas de Cancún con opciones de buceo y snorkel', 1200.00, 'Cancún, México', 'Incluye hotel 5 estrellas, transporte, y tours acuáticos', 'APPROVED', 4.6, NOW(), NOW(), NOW()),
('Tokio: Tradición Futura', 'Explora 10 días la mezcla de tradición y modernidad en Japón', 2000.00, 'Tokio, Japón', 'Templos, tecnología y gastronomía japonesa', 'PENDING', 0.0, NOW(), NOW(), NULL);

-- Insertar blogs aprobados
INSERT INTO blogs (title, content, featured_image, author_id, status, created_at, updated_at, approved_at, view_count) VALUES
('Los Mejores Destinos para 2024', 'Descubre los destinos más fascinantes del año...', 'https://example.com/image1.jpg', 2, 'APPROVED', NOW(), NOW(), NOW(), 150),
('Consejos para Viajar con Presupuesto', 'Cómo disfrutar de viajes increíbles sin gastar mucho...', 'https://example.com/image2.jpg', 2, 'APPROVED', NOW(), NOW(), NOW(), 320);

-- Nota: Las contraseñas son: "password123" encriptadas con BCrypt
-- Puedes cambiar las contraseñas según necesites
