package com.camisastienda.domain.exception;

public class CredencialesInvalidasException extends ReglaNegocioException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }

    public CredencialesInvalidasException() {
        super("Credenciales inválidas. Email o contraseña incorrectos.");
    }
}
