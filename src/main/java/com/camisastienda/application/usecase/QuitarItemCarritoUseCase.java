package com.camisastienda.application.usecase;

import com.camisastienda.application.port.in.QuitarItemCarritoPort;
import com.camisastienda.application.port.out.CarritoRepositoryPort;
import com.camisastienda.domain.exception.RecursoNoEncontradoException;
import com.camisastienda.domain.model.Carrito;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: quitar un ítem del carrito del cliente (solo CLIENTE).
 * Lanza 404 si el usuario no tiene carrito o el ítem no existe en él.
 */
@Service
@Transactional
public class QuitarItemCarritoUseCase implements QuitarItemCarritoPort {

    private final CarritoRepositoryPort carritoRepository;

    public QuitarItemCarritoUseCase(CarritoRepositoryPort carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    @Override
    public Carrito ejecutar(Long usuarioId, Long itemId) {
        Carrito carrito = carritoRepository.buscarPorUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no tiene un carrito"));

        boolean existe = carrito.buscarItemPorId(itemId).isPresent();
        if (!existe) {
            throw new RecursoNoEncontradoException("El ítem " + itemId + " no existe en el carrito");
        }

        carrito.quitarItemPorItemId(itemId);
        return carritoRepository.guardar(carrito);
    }
}
