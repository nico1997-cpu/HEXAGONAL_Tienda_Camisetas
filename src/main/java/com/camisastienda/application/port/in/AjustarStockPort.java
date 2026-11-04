package com.camisastienda.application.port.in;

import com.camisastienda.domain.model.ProductoTalla;

/** Puerto de entrada: establecer el stock de una talla de un producto. */
public interface AjustarStockPort {

    ProductoTalla ejecutar(Long productoId, String tallaStr, int nuevoStock);
}
