package com.camisastienda.domain.exception;

public class CarritoVacioException extends ReglaNegocioException {
    public CarritoVacioException(String mensaje) {
        super(mensaje);
    }

    public CarritoVacioException() {
        super("El carrito está vacío. No se puede realizar la operación.");
    }
}
