package com.camisastienda.application.port.out;

import com.camisastienda.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryPort {
    Optional<Usuario> buscarPorId(Long id);
    Optional<Usuario> buscarPorEmail(String email);
    boolean existePorEmail(String email);
    Usuario guardar(Usuario usuario);
}
