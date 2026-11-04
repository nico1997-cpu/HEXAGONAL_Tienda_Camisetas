package com.camisastienda.application.port.in;

import com.camisastienda.domain.model.Orden;

/** Puerto de entrada: finalizar la compra a partir del carrito del usuario. */
public interface CheckoutPort {

    Orden ejecutar(Long usuarioId);
}
