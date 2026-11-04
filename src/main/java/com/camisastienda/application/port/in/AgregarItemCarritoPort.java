package com.camisastienda.application.port.in;

import com.camisastienda.domain.model.Carrito;

/** Puerto de entrada: agregar un producto/talla al carrito del usuario. */
public interface AgregarItemCarritoPort {

    Carrito ejecutar(Long usuarioId, Long productoId, String tallaStr, int cantidad);
}
