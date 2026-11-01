package com.camisastienda.domain.exception;

/** Excepción base de reglas de negocio (dominio puro, sin Spring). */
public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) {
        super(message);
    }
}
