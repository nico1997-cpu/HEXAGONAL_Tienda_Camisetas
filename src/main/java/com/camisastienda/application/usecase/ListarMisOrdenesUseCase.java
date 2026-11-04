package com.camisastienda.application.usecase;

import com.camisastienda.application.port.in.ListarMisOrdenesPort;
import com.camisastienda.application.port.out.OrdenRepositoryPort;
import com.camisastienda.domain.model.Orden;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de uso: consultar el historial de órdenes del cliente autenticado.
 */
@Service
@Transactional(readOnly = true)
public class ListarMisOrdenesUseCase implements ListarMisOrdenesPort {

    private final OrdenRepositoryPort ordenRepository;

    public ListarMisOrdenesUseCase(OrdenRepositoryPort ordenRepository) {
        this.ordenRepository = ordenRepository;
    }

    @Override
    public List<Orden> ejecutar(Long usuarioId) {
        return ordenRepository.buscarPorUsuarioId(usuarioId);
    }
}

