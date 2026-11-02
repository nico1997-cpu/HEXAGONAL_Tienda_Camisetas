package com.camisastienda.domain.exception;

/** Se lanza cuando se busca un usuario y no existe en el sistema (HTTP 404). */
public class UsuarioNoEncontradoException extends RecursoNoEncontradoException {
    public UsuarioNoEncontradoException(Long id) {
        super("Usuario no encontrado con id: " + id);
    }

    public UsuarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
