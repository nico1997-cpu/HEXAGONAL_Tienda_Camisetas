package com.camisastienda.domain.exception;

/** Se lanza cuando se busca una camiseta por ID y no existe (HTTP 404). */
public class ProductoNoEncontradoException extends RecursoNoEncontradoException {
    public ProductoNoEncontradoException(Long id) {
        super("Producto no encontrado con id: " + id);
    }

    public ProductoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
