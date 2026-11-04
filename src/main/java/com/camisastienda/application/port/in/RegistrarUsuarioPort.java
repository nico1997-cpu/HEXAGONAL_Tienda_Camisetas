package com.camisastienda.application.port.in;

import com.camisastienda.domain.model.Usuario;

/** Puerto de entrada: registrar un nuevo usuario. */
public interface RegistrarUsuarioPort {

    Usuario ejecutar(String nombre, String email, String passwordPlano);
}
