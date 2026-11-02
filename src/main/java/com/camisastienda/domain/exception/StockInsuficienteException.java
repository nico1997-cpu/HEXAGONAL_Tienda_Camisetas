package com.camisastienda.domain.exception;

public class StockInsuficienteException extends ReglaNegocioException {
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }

    public StockInsuficienteException(String producto, String talla, int solicitado, int disponible) {
        super(String.format("Stock insuficiente para %s en talla %s. Solicitado: %d, disponible: %d",
                producto, talla, solicitado, disponible));
    }
}
