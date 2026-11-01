package com.camisastienda.domain.exception;

/** Se lanza cuando un recurso solicitado no existe (HTTP 404). */
public class RecursoNoEncontradoException extends DomainException {
    public RecursoNoEncontradoException(String message) {
        super(message);
    }
}
