package com.camisastienda.application.usecase;

import com.camisastienda.application.port.in.ListarProductosPort;
import com.camisastienda.application.port.out.ProductoRepositoryPort;
import com.camisastienda.domain.model.Producto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de uso: listar camisetas con filtros opcionales (equipo, liga, nombre, talla) y paginación.
 * Si page/size son negativos se devuelven todos los resultados sin paginar.
 */
@Service
@Transactional(readOnly = true)
public class ListarProductosUseCase implements ListarProductosPort {

    private final ProductoRepositoryPort productoRepository;

    public ListarProductosUseCase(ProductoRepositoryPort productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> ejecutar(String equipo, String liga, String nombre, String talla,
                                   int page, int size) {
        String eq = blankToNull(equipo);
        String li = blankToNull(liga);
        String no = blankToNull(nombre);

        List<Producto> productos;
        if (page >= 0 && size > 0) {
            productos = productoRepository.buscarConFiltrosPaginado(eq, li, no, true, page, size);
        } else {
            productos = productoRepository.buscarConFiltros(eq, li, no, true);
        }

        if (talla != null && !talla.isBlank()) {
            String tallaUpper = talla.trim().toUpperCase();
            productos = productos.stream()
                    .filter(p -> p.getTallas().stream()
                            .anyMatch(pt -> pt.getTalla().name().equals(tallaUpper) && pt.getStock() > 0))
                    .toList();
        }

        return productos;
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
