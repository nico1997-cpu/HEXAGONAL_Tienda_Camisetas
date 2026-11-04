package com.camisastienda.application.usecase;

import com.camisastienda.application.port.in.AgregarItemCarritoPort;
import com.camisastienda.application.port.out.CarritoRepositoryPort;
import com.camisastienda.application.port.out.ProductoRepositoryPort;
import com.camisastienda.domain.exception.ProductoNoEncontradoException;
import com.camisastienda.domain.exception.RecursoNoEncontradoException;
import com.camisastienda.domain.exception.ReglaNegocioException;
import com.camisastienda.domain.exception.StockInsuficienteException;
import com.camisastienda.domain.model.Carrito;
import com.camisastienda.domain.model.CarritoItem;
import com.camisastienda.domain.model.Producto;
import com.camisastienda.domain.model.ProductoTalla;
import com.camisastienda.domain.model.Talla;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: agregar un ítem al carrito del cliente (solo CLIENTE).
 * Reglas:
 *  - La talla debe existir para ese producto (si no, 404).
 *  - La cantidad ya en carrito + la nueva no puede superar el stock disponible
 *    (el stock NO se reserva aquí, se descuenta en el checkout).
 *  - Si el mismo producto+talla ya está en el carrito, el dominio suma la cantidad.
 */
@Service
@Transactional
public class AgregarItemCarritoUseCase implements AgregarItemCarritoPort {

    private final CarritoRepositoryPort carritoRepository;
    private final ProductoRepositoryPort productoRepository;

    public AgregarItemCarritoUseCase(CarritoRepositoryPort carritoRepository,
                                     ProductoRepositoryPort productoRepository) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public Carrito ejecutar(Long usuarioId, Long productoId, String tallaStr, int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor a cero");
        }

        Talla talla = parsearTalla(tallaStr);

        Producto producto = productoRepository.buscarPorId(productoId)
                .orElseThrow(() -> new ProductoNoEncontradoException(productoId));

        ProductoTalla productoTalla = producto.buscarTalla(talla)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El producto " + productoId + " no tiene disponible la talla " + talla.name()));

        Carrito carrito = carritoRepository.buscarPorUsuarioId(usuarioId)
                .orElseGet(() -> Carrito.paraUsuario(usuarioId));

        // El stock se valida contra lo que ya está en el carrito + lo nuevo
        int cantidadYaEnCarrito = carrito.buscarItemPorProductoTalla(productoTalla.getId())
                .map(CarritoItem::getCantidad)
                .orElse(0);
        int cantidadTotal = cantidadYaEnCarrito + cantidad;
        if (cantidadTotal > productoTalla.getStock()) {
            throw new StockInsuficienteException(producto.getNombre(), talla.name(), cantidadTotal, productoTalla.getStock());
        }

        carrito.agregarItem(CarritoItem.crear(
                productoTalla.getId(), producto.getNombre(), talla, producto.getPrecio(), cantidad));

        return carritoRepository.guardar(carrito);
    }

    private Talla parsearTalla(String tallaStr) {
        try {
            return Talla.desdeTexto(tallaStr);
        } catch (IllegalArgumentException e) {
            throw new ReglaNegocioException(e.getMessage());
        }
    }
}
