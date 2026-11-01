package com.camisastienda.domain.exception;

/** Violación de una regla de negocio, p. ej. stock insuficiente (HTTP 409). */
public class ReglaNegocioException extends DomainException {
    public ReglaNegocioException(String message) {
        super(message);
    }
}
