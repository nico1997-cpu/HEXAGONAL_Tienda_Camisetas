package com.camisastienda.application.usecase;

import com.camisastienda.application.port.out.ProductoRepositoryPort;
import com.camisastienda.domain.exception.ProductoNoEncontradoException;
import com.camisastienda.domain.model.Producto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: obtener el detalle completo de una camiseta (con todas sus tallas y stock).
 * Lanza ProductoNoEncontradoException (404) si el ID no existe.
 */
@Service
@Transactional(readOnly = true)
public class VerDetalleProductoUseCase {

    private final ProductoRepositoryPort productoRepository;

    public VerDetalleProductoUseCase(ProductoRepositoryPort productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Producto ejecutar(Long id) {
        return productoRepository.buscarPorId(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }
}
