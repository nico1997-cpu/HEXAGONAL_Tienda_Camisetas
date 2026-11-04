package com.camisastienda.application.port.in;

import com.camisastienda.domain.model.Producto;
import java.util.List;

/** Puerto de entrada: listar productos con filtros y paginación opcionales. */
public interface ListarProductosPort {

    List<Producto> ejecutar(String equipo, String liga, String nombre, String talla, int page, int size);
}
