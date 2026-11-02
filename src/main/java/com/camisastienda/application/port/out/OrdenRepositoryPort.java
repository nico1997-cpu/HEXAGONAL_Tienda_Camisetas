package com.camisastienda.application.port.out;

import com.camisastienda.domain.model.Orden;

import java.util.List;
import java.util.Optional;

public interface OrdenRepositoryPort {
    Orden guardar(Orden orden);
    Optional<Orden> buscarPorId(Long id);
    List<Orden> buscarPorUsuarioId(Long usuarioId);
}
