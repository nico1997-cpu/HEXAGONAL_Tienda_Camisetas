package com.camisastienda.domain.model;

import com.camisastienda.domain.exception.CarritoVacioException;
import com.camisastienda.domain.exception.ReglaNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrdenTest {

    @Test
    @DisplayName("Debe crear una orden desde un carrito congelando los precios")
    void crearDesdeCarrito_carritoValido_creaOrdenConTotalCongelado() {
        Carrito carrito = Carrito.paraUsuario(5L);
        carrito.agregarItem(CarritoItem.crear(1L, "Camiseta Real Madrid", Talla.M, new BigDecimal("90.00"), 2));
        carrito.agregarItem(CarritoItem.crear(2L, "Camiseta Colombia", Talla.L, new BigDecimal("100.00"), 1));

        Orden orden = Orden.crearDesdeCarrito(carrito, 5L);

        assertThat(orden.getUsuarioId()).isEqualTo(5L);
        assertThat(orden.getEstado()).isEqualTo(EstadoOrden.CREADA);
        assertThat(orden.getItems()).hasSize(2);
        assertThat(orden.getTotal()).isEqualByComparingTo(new BigDecimal("280.00"));
    }

    @Test
    @DisplayName("Debe lanzar CarritoVacioException si el carrito está vacío")
    void crearDesdeCarrito_carritoVacio_lanzaExcepcion() {
        Carrito carrito = Carrito.paraUsuario(5L);
        assertThatThrownBy(() -> Orden.crearDesdeCarrito(carrito, 5L))
                .isInstanceOf(CarritoVacioException.class);
    }

    @Test
    @DisplayName("Debe permitir flujo de estados: CREADA -> PAGADA -> COMPLETADA")
    void flujoEstados_transicionesValidas_actualizaCorrectamente() {
        Carrito carrito = Carrito.paraUsuario(1L);
        carrito.agregarItem(CarritoItem.crear(1L, "Camiseta", Talla.S, BigDecimal.TEN, 1));

        Orden orden = Orden.crearDesdeCarrito(carrito, 1L);
        assertThat(orden.getEstado()).isEqualTo(EstadoOrden.CREADA);

        orden.pagar();
        assertThat(orden.getEstado()).isEqualTo(EstadoOrden.PAGADA);

        orden.completar();
        assertThat(orden.getEstado()).isEqualTo(EstadoOrden.COMPLETADA);
    }

    @Test
    @DisplayName("No debe permitir cancelar una orden ya completada")
    void cancelar_ordenCompletada_lanzaExcepcion() {
        Carrito carrito = Carrito.paraUsuario(1L);
        carrito.agregarItem(CarritoItem.crear(1L, "Camiseta", Talla.S, BigDecimal.TEN, 1));

        Orden orden = Orden.crearDesdeCarrito(carrito, 1L);
        orden.pagar();
        orden.completar();

        assertThatThrownBy(orden::cancelar)
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ya fue completada");
    }
}
