package com.camisastienda.application.usecase;

import com.camisastienda.application.port.out.ProductoRepositoryPort;
import com.camisastienda.domain.exception.ProductoNoEncontradoException;
import com.camisastienda.domain.model.Producto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Caso de uso: actualizar los datos de una camiseta existente (solo ADMIN).
 * Lanza ProductoNoEncontradoException si el ID no existe.
 */
@Service
@Transactional
public class ActualizarProductoUseCase {

    private final ProductoRepositoryPort productoRepository;

    public ActualizarProductoUseCase(ProductoRepositoryPort productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Producto ejecutar(Long id, String nombre, String descripcion, String equipo,
                             String liga, String temporada, BigDecimal precio, String imagenUrl) {
        Producto producto = productoRepository.buscarPorId(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));

        producto.setNombre(nombre.trim());
        producto.setDescripcion(descripcion != null ? descripcion.trim() : null);
        producto.setEquipo(equipo.trim());
        producto.setLiga(liga.trim());
        producto.setTemporada(temporada.trim());
        producto.setPrecio(precio);
        producto.setImagenUrl(imagenUrl);

        return productoRepository.guardar(producto);
    }
}
