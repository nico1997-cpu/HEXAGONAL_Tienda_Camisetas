package com.camisastienda.application.port.out;

import com.camisastienda.domain.model.Carrito;

import java.util.Optional;

/** Puerto de salida para consultar y guardar el carrito de un usuario. */
public interface CarritoRepositoryPort {
    Optional<Carrito> buscarPorUsuarioId(Long usuarioId);
    Carrito guardar(Carrito carrito);
    void eliminarPorUsuarioId(Long usuarioId);
}
