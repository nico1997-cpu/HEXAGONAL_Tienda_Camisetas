-- =====================================================================
-- Flyway Migration V3: Corrige los hashes BCrypt de los usuarios semilla
-- Tienda de Camisetas - Arquitectura Hexagonal
-- =====================================================================
-- Los hashes de V1/V2 no correspondían a las contraseñas documentadas
-- (admin123 / cliente123), por lo que el login devolvía 401.
-- Esta migración los reemplaza por hashes BCrypt válidos generados
-- con BCryptPasswordEncoder (Spring Security, factor 10).

UPDATE usuarios
SET password_hash = '$2a$10$P0Ep8KE0ji4byD0SzU.TBeEEnnZqS3q2jJP3DkkaM3NC7bLgYfw2i'
WHERE email = 'admin@camisastienda.com';

UPDATE usuarios
SET password_hash = '$2a$10$nbY/o.QhfaFd70b/m4p9QeiJzA3Wd0TClQJtSArpR7.1CEr65zuJG'
WHERE email = 'cliente@camisastienda.com';
