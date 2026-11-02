package com.camisastienda.domain.model;

/** Tallas permitidas para las camisetas deportivas. */
public enum Talla {
    XS,
    S,
    M,
    L,
    XL,
    XXL;

    // Convierte un texto (ej. "m") al enum Talla validando que exista
    public static Talla desdeTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("La talla no puede ser vacía");
        }
        try {
            return Talla.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Talla no válida: " + texto + ". Valores permitidos: XS, S, M, L, XL, XXL");
        }
    }
}
