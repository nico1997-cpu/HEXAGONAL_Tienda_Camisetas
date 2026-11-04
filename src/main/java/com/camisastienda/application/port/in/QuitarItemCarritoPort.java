package com.camisastienda.application.port.in;

import com.camisastienda.domain.model.Carrito;

/** Puerto de entrada: quitar un ítem del carrito del usuario. */
public interface QuitarItemCarritoPort {

    Carrito ejecutar(Long usuarioId, Long itemId);
}
