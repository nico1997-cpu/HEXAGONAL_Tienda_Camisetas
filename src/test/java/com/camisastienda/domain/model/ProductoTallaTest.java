package com.camisastienda.domain.model;

import com.camisastienda.domain.exception.ReglaNegocioException;
import com.camisastienda.domain.exception.StockInsuficienteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductoTallaTest {

    @Test
    @DisplayName("Debe descontar stock correctamente cuando hay suficiente")
    void descontarStock_conStockSuficiente_descuentaCorrectamente() {
        ProductoTalla pt = ProductoTalla.nueva(1L, Talla.M, 10);
        pt.descontarStock(3);
        assertThat(pt.getStock()).isEqualTo(7);
    }

    @Test
    @DisplayName("Debe lanzar StockInsuficienteException si se solicita más del disponible")
    void descontarStock_conStockInsuficiente_lanzaExcepcion() {
        ProductoTalla pt = ProductoTalla.nueva(1L, Talla.L, 5);
        assertThatThrownBy(() -> pt.descontarStock(6))
                .isInstanceOf(StockInsuficienteException.class)
                .hasMessageContaining("Stock insuficiente");
    }

    @Test
    @DisplayName("Debe lanzar excepción si se intenta descontar una cantidad menor o igual a cero")
    void descontarStock_cantidadInvalida_lanzaExcepcion() {
        ProductoTalla pt = ProductoTalla.nueva(1L, Talla.S, 5);
        assertThatThrownBy(() -> pt.descontarStock(0))
                .isInstanceOf(ReglaNegocioException.class);
        assertThatThrownBy(() -> pt.descontarStock(-2))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    @DisplayName("Debe aumentar stock correctamente")
    void aumentarStock_cantidadValida_aumentaStock() {
        ProductoTalla pt = ProductoTalla.nueva(1L, Talla.XL, 5);
        pt.aumentarStock(10);
        assertThat(pt.getStock()).isEqualTo(15);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar aumentar una cantidad no positiva")
    void aumentarStock_cantidadInvalida_lanzaExcepcion() {
        ProductoTalla pt = ProductoTalla.nueva(1L, Talla.XL, 5);
        assertThatThrownBy(() -> pt.aumentarStock(0))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    @DisplayName("Debe validar que el stock inicial no sea negativo")
    void crear_stockNegativo_lanzaExcepcion() {
        assertThatThrownBy(() -> new ProductoTalla(1L, 1L, Talla.M, -1, 0L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("El stock no puede ser negativo");
    }

    @Test
    @DisplayName("hayStock debe retornar true si hay suficiente y false si no")
    void hayStock_verificaDisponibilidad() {
        ProductoTalla pt = ProductoTalla.nueva(1L, Talla.M, 4);
        assertThat(pt.hayStock(4)).isTrue();
        assertThat(pt.hayStock(5)).isFalse();
        assertThat(pt.hayStock(0)).isFalse();
    }
}
