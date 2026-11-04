package com.camisastienda.application.usecase;

import com.camisastienda.application.port.in.VerDetalleOrdenPort;
import com.camisastienda.application.port.out.OrdenRepositoryPort;
import com.camisastienda.domain.exception.RecursoNoEncontradoException;
import com.camisastienda.domain.model.Orden;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Caso de uso: ver el detalle de una orden de compra por ID.
 * Valida que la orden pertenezca al usuario autenticado (lanza 404 si no pertenece o no existe).
 */
@Service
@Transactional(readOnly = true)
public class VerDetalleOrdenUseCase implements VerDetalleOrdenPort {

    private final OrdenRepositoryPort ordenRepository;

    public VerDetalleOrdenUseCase(OrdenRepositoryPort ordenRepository) {
        this.ordenRepository = ordenRepository;
    }

    @Override
    public Orden ejecutar(Long usuarioId, Long ordenId) {
        Orden orden = ordenRepository.buscarPorId(ordenId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + ordenId));

        if (!Objects.equals(orden.getUsuarioId(), usuarioId)) {
            throw new RecursoNoEncontradoException("Orden no encontrada con ID: " + ordenId);
        }

        return orden;
    }
}

