package com.camisastienda.application.port.in;

import com.camisastienda.domain.model.Producto;
import java.math.BigDecimal;

/** Puerto de entrada: crear un nuevo producto en el catálogo. */
public interface CrearProductoPort {

    Producto ejecutar(String nombre, String descripcion, String equipo, String liga, String temporada, BigDecimal precio, String imagenUrl);
}
