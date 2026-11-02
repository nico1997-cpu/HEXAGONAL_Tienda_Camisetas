package com.camisastienda.domain.exception;

public class ProductoNoEncontradoException extends RecursoNoEncontradoException {
    public ProductoNoEncontradoException(Long id) {
        super("Producto no encontrado con id: " + id);
    }

    public ProductoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
