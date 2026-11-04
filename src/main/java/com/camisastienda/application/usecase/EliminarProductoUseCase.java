package com.camisastienda.application.usecase;

import com.camisastienda.application.port.in.EliminarProductoPort;
import com.camisastienda.application.port.out.ProductoRepositoryPort;
import com.camisastienda.domain.exception.ProductoNoEncontradoException;
import com.camisastienda.domain.model.Producto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: eliminar una camiseta de forma lógica (activo = false) — solo ADMIN.
 * No elimina físicamente el registro para preservar historial de órdenes.
 */
@Service
@Transactional
public class EliminarProductoUseCase implements EliminarProductoPort {

    private final ProductoRepositoryPort productoRepository;

    public EliminarProductoUseCase(ProductoRepositoryPort productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public void ejecutar(Long id) {
        Producto producto = productoRepository.buscarPorId(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
        producto.desactivar();
        productoRepository.guardar(producto);
    }
}
