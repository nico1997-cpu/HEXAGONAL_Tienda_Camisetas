package com.camisastienda.application.usecase;

import com.camisastienda.application.port.in.CheckoutPort;
import com.camisastienda.application.port.out.CarritoRepositoryPort;
import com.camisastienda.application.port.out.OrdenRepositoryPort;
import com.camisastienda.application.port.out.ProductoRepositoryPort;
import com.camisastienda.domain.exception.CarritoVacioException;
import com.camisastienda.domain.exception.RecursoNoEncontradoException;
import com.camisastienda.domain.model.Carrito;
import com.camisastienda.domain.model.CarritoItem;
import com.camisastienda.domain.model.Orden;
import com.camisastienda.domain.model.ProductoTalla;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: finalizar compra (checkout) a partir del carrito del cliente.
 * En una sola transacción:
 *  1. Valida que el carrito exista y no esté vacío (409 si está vacío).
 *  2. Descuenta el stock de cada talla (409 si stock insuficiente -> rollback).
 *  3. Crea la orden congelando los precios actuales del carrito.
 *  4. Persiste la orden en estado CREADA.
 *  5. Vacía el carrito del usuario.
 */
@Service
@Transactional
public class CheckoutUseCase implements CheckoutPort {

    private final CarritoRepositoryPort carritoRepository;
    private final ProductoRepositoryPort productoRepository;
    private final OrdenRepositoryPort ordenRepository;

    public CheckoutUseCase(CarritoRepositoryPort carritoRepository,
                           ProductoRepositoryPort productoRepository,
                           OrdenRepositoryPort ordenRepository) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
        this.ordenRepository = ordenRepository;
    }

    @Override
    public Orden ejecutar(Long usuarioId) {
        Carrito carrito = carritoRepository.buscarPorUsuarioId(usuarioId)
                .orElseThrow(CarritoVacioException::new);

        if (carrito.estaVacio()) {
            throw new CarritoVacioException();
        }

        // Descontar stock de cada ítem en el inventario
        for (CarritoItem item : carrito.getItems()) {
            ProductoTalla talla = productoRepository.buscarTallaPorId(item.getProductoTallaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "La talla con ID " + item.getProductoTallaId() + " no fue encontrada"));

            talla.descontarStock(item.getCantidad());
            productoRepository.guardarTalla(talla);
        }

        // Crear la orden congelando precios
        Orden orden = Orden.crearDesdeCarrito(carrito, usuarioId);
        Orden ordenGuardada = ordenRepository.guardar(orden);

        // Vaciar el carrito tras compra exitosa
        carrito.vaciar();
        carritoRepository.guardar(carrito);

        return ordenGuardada;
    }
}

