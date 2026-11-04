package com.camisastienda.application.usecase;

import com.camisastienda.application.port.in.VaciarCarritoPort;
import com.camisastienda.application.port.out.CarritoRepositoryPort;
import com.camisastienda.domain.model.Carrito;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: vaciar por completo el carrito del cliente (solo CLIENTE).
 * Si el usuario no tiene carrito, la operación es un no-op (no falla).
 */
@Service
@Transactional
public class VaciarCarritoUseCase implements VaciarCarritoPort {

    private final CarritoRepositoryPort carritoRepository;

    public VaciarCarritoUseCase(CarritoRepositoryPort carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    @Override
    public void ejecutar(Long usuarioId) {
        carritoRepository.buscarPorUsuarioId(usuarioId).ifPresent(carrito -> {
            carrito.vaciar();
            carritoRepository.guardar(carrito);
        });
    }
}
