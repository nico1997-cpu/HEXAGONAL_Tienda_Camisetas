package com.camisastienda.application.port.in;

/** Puerto de entrada: vaciar el carrito del usuario. */
public interface VaciarCarritoPort {

    void ejecutar(Long usuarioId);
}
