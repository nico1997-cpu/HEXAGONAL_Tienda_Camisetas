package com.camisastienda.application.port.out;

import com.camisastienda.domain.model.Usuario;

import java.util.Optional;

/** Puerto de salida para buscar y guardar usuarios en la base de datos. */
public interface UsuarioRepositoryPort {
    Optional<Usuario> buscarPorId(Long id);
    Optional<Usuario> buscarPorEmail(String email);
    boolean existePorEmail(String email);
    Usuario guardar(Usuario usuario);
}
