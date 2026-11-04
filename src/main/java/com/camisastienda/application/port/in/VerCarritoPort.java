package com.camisastienda.application.port.in;

import com.camisastienda.domain.model.Carrito;

/** Puerto de entrada: consultar el carrito del usuario. */
public interface VerCarritoPort {

    Carrito ejecutar(Long usuarioId);
}
