package com.camisastienda.application.port.out;

import com.camisastienda.domain.model.Carrito;

import java.util.Optional;

public interface CarritoRepositoryPort {
    Optional<Carrito> buscarPorUsuarioId(Long usuarioId);
    Carrito guardar(Carrito carrito);
    void eliminarPorUsuarioId(Long usuarioId);
}
