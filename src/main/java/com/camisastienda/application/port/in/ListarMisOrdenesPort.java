package com.camisastienda.application.port.in;

import com.camisastienda.domain.model.Orden;
import java.util.List;

/** Puerto de entrada: listar las órdenes de un usuario. */
public interface ListarMisOrdenesPort {

    List<Orden> ejecutar(Long usuarioId);
}
