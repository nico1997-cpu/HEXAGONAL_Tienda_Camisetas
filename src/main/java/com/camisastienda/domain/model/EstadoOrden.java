package com.camisastienda.domain.model;

/** Estados posibles de una orden de compra. */
public enum EstadoOrden {
    CREADA,
    CANCELADA,
    PAGADA,
    COMPLETADA;

    // Convierte texto al enum EstadoOrden
    public static EstadoOrden desdeTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El estado de la orden no puede ser vacío");
        }
        try {
            return EstadoOrden.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado no válido: " + texto + ". Valores permitidos: CREADA, CANCELADA, PAGADA, COMPLETADA");
        }
    }
}
