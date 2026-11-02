package com.camisastienda.domain.model;

/** Roles de usuario: ADMIN (gestiona catálogo/stock) y CLIENTE (compra). */
public enum Rol {
    ADMIN,
    CLIENTE;

    // Convierte texto al enum Rol validando que sea válido
    public static Rol desdeTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El rol no puede ser vacío");
        }
        try {
            return Rol.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol no válido: " + texto + ". Valores permitidos: ADMIN, CLIENTE");
        }
    }
}
