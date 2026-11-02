package com.camisastienda.domain.exception;

/** Se lanza cuando se intenta comprar con un carrito sin productos (HTTP 409). */
public class CarritoVacioException extends ReglaNegocioException {
    public CarritoVacioException(String mensaje) {
        super(mensaje);
    }

    public CarritoVacioException() {
        super("El carrito está vacío. No se puede realizar la operación.");
    }
}
