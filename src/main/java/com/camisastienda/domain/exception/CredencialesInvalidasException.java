package com.camisastienda.domain.exception;

/** Se lanza cuando el email o la contraseña en el login son incorrectos. */
public class CredencialesInvalidasException extends ReglaNegocioException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }

    public CredencialesInvalidasException() {
        super("Credenciales inválidas. Email o contraseña incorrectos.");
    }
}
