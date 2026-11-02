package com.camisastienda.application.usecase;

import com.camisastienda.application.port.out.ProductoRepositoryPort;
import com.camisastienda.domain.exception.ProductoNoEncontradoException;
import com.camisastienda.domain.exception.ReglaNegocioException;
import com.camisastienda.domain.model.Producto;
import com.camisastienda.domain.model.ProductoTalla;
import com.camisastienda.domain.model.Talla;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: ajustar el stock de una talla concreta (solo ADMIN).
 * Si la talla no existe en el producto la crea; si ya existe actualiza el stock.
 * Lanza ReglaNegocioException si stock < 0.
 */
@Service
@Transactional
public class AjustarStockUseCase {

    private final ProductoRepositoryPort productoRepository;

    public AjustarStockUseCase(ProductoRepositoryPort productoRepository) {
        this.productoRepository = productoRepository;
    }

    public ProductoTalla ejecutar(Long productoId, String tallaStr, int nuevoStock) {
        if (nuevoStock < 0) {
            throw new ReglaNegocioException("El stock no puede ser negativo");
        }

        Producto producto = productoRepository.buscarPorId(productoId)
                .orElseThrow(() -> new ProductoNoEncontradoException(productoId));

        Talla talla = Talla.desdeTexto(tallaStr);

        ProductoTalla productoTalla = producto.buscarTalla(talla)
                .orElse(null);

        if (productoTalla == null) {
            // Talla nueva: crearla y persistir
            ProductoTalla nueva = ProductoTalla.nueva(productoId, talla, nuevoStock);
            return productoRepository.guardarTalla(nueva);
        } else {
            // Talla existente: actualizar stock directamente
            productoTalla.setStock(nuevoStock);
            return productoRepository.guardarTalla(productoTalla);
        }
    }
}
