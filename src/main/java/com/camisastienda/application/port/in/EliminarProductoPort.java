package com.camisastienda.application.port.in;

/** Puerto de entrada: eliminar (borrado lógico) un producto. */
public interface EliminarProductoPort {

    void ejecutar(Long id);
}
