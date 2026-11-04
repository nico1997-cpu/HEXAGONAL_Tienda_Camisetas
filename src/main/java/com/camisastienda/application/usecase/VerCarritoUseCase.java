package com.camisastienda.application.usecase;

import com.camisastienda.application.port.in.VerCarritoPort;
import com.camisastienda.application.port.out.CarritoRepositoryPort;
import com.camisastienda.domain.model.Carrito;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: ver el carrito del cliente con subtotales y total (solo CLIENTE).
 * Si el usuario aún no tiene carrito, devuelve uno vacío sin persistir;
 * el carrito se crea en BD la primera vez que agrega un ítem.
 */
@Service
@Transactional(readOnly = true)
public class VerCarritoUseCase implements VerCarritoPort {

    private final CarritoRepositoryPort carritoRepository;

    public VerCarritoUseCase(CarritoRepositoryPort carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    @Override
    public Carrito ejecutar(Long usuarioId) {
        return carritoRepository.buscarPorUsuarioId(usuarioId)
                .orElseGet(() -> Carrito.paraUsuario(usuarioId));
    }
}
