-- =====================================================================
-- Base de datos: camisas_tienda
-- Datos de ejemplo iniciales (seed data)
-- Arquitectura Hexagonal - SENA
-- =====================================================================

USE camisas_tienda;

-- 1. Usuarios iniciales con contraseñas BCrypt válidas:
--    admin@camisastienda.com   -> admin123
--    cliente@camisastienda.com -> cliente123
INSERT INTO usuarios (id, nombre, email, password_hash, rol, creado_en) VALUES
(1, 'Administrador del Sistema', 'admin@camisastienda.com', '$2a$10$P0Ep8KE0ji4byD0SzU.TBeEEnnZqS3q2jJP3DkkaM3NC7bLgYfw2i', 'ADMIN', CURRENT_TIMESTAMP),
(2, 'Cliente de Prueba', 'cliente@camisastienda.com', '$2a$10$nbY/o.QhfaFd70b/m4p9QeiJzA3Wd0TClQJtSArpR7.1CEr65zuJG', 'CLIENTE', CURRENT_TIMESTAMP);

-- 2. Catálogo de Camisetas
INSERT INTO productos (id, nombre, descripcion, equipo, liga, temporada, precio, imagen_url, activo, creado_en) VALUES
(1, 'Camiseta Real Madrid Local 24/25', 'Camiseta oficial de juego local del Real Madrid temporada 2024/2025 con tejido transpirable.', 'Real Madrid', 'LaLiga', '2024/2025', 89.99, 'https://images.unsplash.com/photo-1522778119026-d647f0596c20', TRUE, CURRENT_TIMESTAMP),
(2, 'Camiseta FC Barcelona Local 24/25', 'Camiseta conmemorativa 125 aniversario con los colores tradicionales azulgrana.', 'FC Barcelona', 'LaLiga', '2024/2025', 89.99, 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2', TRUE, CURRENT_TIMESTAMP),
(3, 'Camiseta Manchester City Local 24/25', 'Camiseta oficial con detalles del código postal 0161 de Manchester en cuello y puños.', 'Manchester City', 'Premier League', '2024/2025', 94.99, 'https://images.unsplash.com/photo-1517466787929-bc90951d0974', TRUE, CURRENT_TIMESTAMP),
(4, 'Camiseta Selección Colombia Centenario', 'Edición especial blanca conmemorando los 100 años de la Federación Colombiana de Fútbol.', 'Selección Colombia', 'Selecciones', '2024', 99.99, 'https://images.unsplash.com/photo-1579952363873-27f3bade9f55', TRUE, CURRENT_TIMESTAMP),
(5, 'Camiseta Arsenal Local 24/25', 'Camiseta de local con el icónico cañón minimalista en el pecho.', 'Arsenal', 'Premier League', '2024/2025', 89.99, 'https://images.unsplash.com/photo-1489944440615-453fc2b6a9a9', TRUE, CURRENT_TIMESTAMP);

-- 3. Tallas y Stock por Producto
INSERT INTO producto_tallas (id, producto_id, talla, stock, version) VALUES
-- Real Madrid (id: 1)
(1, 1, 'S', 12, 0),
(2, 1, 'M', 20, 0),
(3, 1, 'L', 15, 0),
(4, 1, 'XL', 8, 0),

-- FC Barcelona (id: 2)
(5, 2, 'S', 10, 0),
(6, 2, 'M', 18, 0),
(7, 2, 'L', 14, 0),
(8, 2, 'XL', 6, 0),

-- Manchester City (id: 3)
(9, 3, 'S', 8, 0),
(10, 3, 'M', 15, 0),
(11, 3, 'L', 10, 0),
(12, 3, 'XL', 5, 0),

-- Selección Colombia (id: 4)
(13, 4, 'S', 15, 0),
(14, 4, 'M', 25, 0),
(15, 4, 'L', 20, 0),
(16, 4, 'XL', 10, 0),

-- Arsenal (id: 5)
(17, 5, 'S', 6, 0),
(18, 5, 'M', 12, 0),
(19, 5, 'L', 8, 0),
(20, 5, 'XL', 4, 0);

-- 4. Carrito inicial para el usuario cliente
INSERT INTO carritos (id, usuario_id, actualizado_en) VALUES
(1, 2, CURRENT_TIMESTAMP);
