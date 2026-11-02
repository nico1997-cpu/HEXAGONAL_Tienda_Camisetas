package com.camisastienda.application.port.out;

import com.camisastienda.domain.model.Usuario;

/** Puerto de salida para generar y validar tokens JWT. */
public interface TokenProviderPort {
    String generarToken(Usuario usuario);
    String extraerEmail(String token);
    boolean validarToken(String token);
    String extraerRol(String token);
}
