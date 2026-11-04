package com.camisastienda.infrastructure.security;

/**
 * Principal que el filtro JWT coloca en el contexto de seguridad
 * a partir de los claims del token: usuarioId, email y rol.
 */
public record UsuarioPrincipal(Long id, String email, String rol) {
}
