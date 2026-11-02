package com.camisastienda.application.port.out;

import com.camisastienda.domain.model.Producto;
import com.camisastienda.domain.model.ProductoTalla;

import java.util.List;
import java.util.Optional;

public interface ProductoRepositoryPort {
    Optional<Producto> buscarPorId(Long id);
    List<Producto> listarTodos(boolean soloActivos);
    List<Producto> buscarConFiltros(String equipo, String liga, String nombre, Boolean soloActivos);
    Producto guardar(Producto producto);
    void eliminarPorId(Long id);

    Optional<ProductoTalla> buscarTallaPorId(Long productoTallaId);
    Optional<ProductoTalla> buscarTallaPorProductoIdYTalla(Long productoId, String talla);
    ProductoTalla guardarTalla(ProductoTalla talla);
}
