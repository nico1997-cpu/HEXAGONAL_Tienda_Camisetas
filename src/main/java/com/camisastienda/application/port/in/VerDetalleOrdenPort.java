package com.camisastienda.application.port.in;

import com.camisastienda.domain.model.Orden;

/** Puerto de entrada: consultar el detalle de una orden del usuario. */
public interface VerDetalleOrdenPort {

    Orden ejecutar(Long usuarioId, Long ordenId);
}
