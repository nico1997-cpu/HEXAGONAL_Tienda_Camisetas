package com.camisastienda.domain.model;

public enum Rol {
    ADMIN,
    CLIENTE;

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
