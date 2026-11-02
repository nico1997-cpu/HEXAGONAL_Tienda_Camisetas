package com.camisastienda.domain.exception;

public class UsuarioNoEncontradoException extends RecursoNoEncontradoException {
    public UsuarioNoEncontradoException(Long id) {
        super("Usuario no encontrado con id: " + id);
    }

    public UsuarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
