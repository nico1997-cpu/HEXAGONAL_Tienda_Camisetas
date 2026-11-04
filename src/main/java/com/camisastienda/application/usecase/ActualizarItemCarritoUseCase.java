package com.camisastienda.application.usecase;

import com.camisastienda.application.port.in.ActualizarItemCarritoPort;
import com.camisastienda.application.port.out.CarritoRepositoryPort;
import com.camisastienda.application.port.out.ProductoRepositoryPort;
import com.camisastienda.domain.exception.RecursoNoEncontradoException;
import com.camisastienda.domain.exception.StockInsuficienteException;
import com.camisastienda.domain.model.Carrito;
import com.camisastienda.domain.model.CarritoItem;
import com.camisastienda.domain.model.ProductoTalla;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: cambiar la cantidad de un ítem del carrito (solo CLIENTE).
 * La nueva cantidad reemplaza a la anterior y se valida contra el stock disponible.
 */
@Service
@Transactional
public class ActualizarItemCarritoUseCase implements ActualizarItemCarritoPort {

    private final CarritoRepositoryPort carritoRepository;
    private final ProductoRepositoryPort productoRepository;

    public ActualizarItemCarritoUseCase(CarritoRepositoryPort carritoRepository,
                                        ProductoRepositoryPort productoRepository) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public Carrito ejecutar(Long usuarioId, Long itemId, int nuevaCantidad) {
        Carrito carrito = carritoRepository.buscarPorUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no tiene un carrito"));

        CarritoItem item = carrito.buscarItemPorId(itemId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El ítem " + itemId + " no existe en el carrito"));

        ProductoTalla productoTalla = productoRepository.buscarTallaPorId(item.getProductoTallaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "La talla del producto ya no está disponible"));

        if (nuevaCantidad > productoTalla.getStock()) {
            throw new StockInsuficienteException(item.getNombreProducto(),
                    item.getTalla().name(), nuevaCantidad, productoTalla.getStock());
        }

        carrito.actualizarCantidadPorItemId(itemId, nuevaCantidad);
        return carritoRepository.guardar(carrito);
    }
}
