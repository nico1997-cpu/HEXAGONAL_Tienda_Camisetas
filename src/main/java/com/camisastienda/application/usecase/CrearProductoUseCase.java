package com.camisastienda.application.usecase;

import com.camisastienda.application.port.out.ProductoRepositoryPort;
import com.camisastienda.domain.model.Producto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Caso de uso: crear una nueva camiseta de fútbol (solo ADMIN).
 * Las validaciones de precio ≥ 0 y campos obligatorios las aplica el dominio (Producto).
 */
@Service
@Transactional
public class CrearProductoUseCase {

    private final ProductoRepositoryPort productoRepository;

    public CrearProductoUseCase(ProductoRepositoryPort productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Producto ejecutar(String nombre, String descripcion, String equipo, String liga,
                             String temporada, BigDecimal precio, String imagenUrl) {
        Producto nuevo = Producto.nuevo(nombre, descripcion, equipo, liga, temporada, precio, imagenUrl);
        return productoRepository.guardar(nuevo);
    }
}
