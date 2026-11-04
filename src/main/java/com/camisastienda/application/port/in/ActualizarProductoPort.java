package com.camisastienda.application.port.in;

import com.camisastienda.domain.model.Producto;
import java.math.BigDecimal;

/** Puerto de entrada: actualizar los datos de un producto (sin tocar tallas ni stock). */
public interface ActualizarProductoPort {

    Producto ejecutar(Long id, String nombre, String descripcion, String equipo, String liga, String temporada, BigDecimal precio, String imagenUrl);
}
