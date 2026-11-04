package com.camisastienda.application.port.in;

import com.camisastienda.domain.model.Producto;

/** Puerto de entrada: consultar el detalle de un producto. */
public interface VerDetalleProductoPort {

    Producto ejecutar(Long id);
}
