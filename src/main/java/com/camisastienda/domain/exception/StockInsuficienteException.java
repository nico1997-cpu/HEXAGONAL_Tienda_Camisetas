package com.camisastienda.domain.exception;

/** Se lanza cuando no hay suficiente stock para una talla solicitada (HTTP 409). */
public class StockInsuficienteException extends ReglaNegocioException {
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }

    public StockInsuficienteException(String producto, String talla, int solicitado, int disponible) {
        super(String.format("Stock insuficiente para %s en talla %s. Solicitado: %d, disponible: %d",
                producto, talla, solicitado, disponible));
    }
}
