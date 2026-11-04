package com.camisastienda.application.port.in;

import com.camisastienda.domain.model.Carrito;

/** Puerto de entrada: cambiar la cantidad de un ítem del carrito del usuario. */
public interface ActualizarItemCarritoPort {

    Carrito ejecutar(Long usuarioId, Long itemId, int nuevaCantidad);
}
