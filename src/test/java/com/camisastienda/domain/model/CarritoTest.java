package com.camisastienda.domain.model;

import com.camisastienda.domain.exception.ReglaNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarritoTest {

    private Carrito carrito;

    @BeforeEach
    void setUp() {
        carrito = Carrito.paraUsuario(1L);
    }

    @Test
    @DisplayName("Debe agregar un nuevo ítem al carrito")
    void agregarItem_nuevoItem_agregaExitosamente() {
        CarritoItem item = CarritoItem.crear(10L, "Camiseta Real Madrid", Talla.M, new BigDecimal("80.00"), 2);
        carrito.agregarItem(item);

        assertThat(carrito.getItems()).hasSize(1);
        assertThat(carrito.contarTotalUnidades()).isEqualTo(2);
        assertThat(carrito.calcularTotal()).isEqualByComparingTo(new BigDecimal("160.00"));
    }

    @Test
    @DisplayName("Debe sumar la cantidad si el ítem ya existe en el carrito")
    void agregarItem_itemExistente_incrementaCantidad() {
        CarritoItem item1 = CarritoItem.crear(10L, "Camiseta Real Madrid", Talla.M, new BigDecimal("80.00"), 2);
        CarritoItem item2 = CarritoItem.crear(10L, "Camiseta Real Madrid", Talla.M, new BigDecimal("80.00"), 3);

        carrito.agregarItem(item1);
        carrito.agregarItem(item2);

        assertThat(carrito.getItems()).hasSize(1);
        assertThat(carrito.contarTotalUnidades()).isEqualTo(5);
        assertThat(carrito.calcularTotal()).isEqualByComparingTo(new BigDecimal("400.00"));
    }

    @Test
    @DisplayName("Debe actualizar cantidad de un ítem existente")
    void actualizarCantidad_itemExistente_actualizaCorrectamente() {
        CarritoItem item = CarritoItem.crear(10L, "Camiseta Real Madrid", Talla.M, new BigDecimal("80.00"), 2);
        carrito.agregarItem(item);

        carrito.actualizarCantidad(10L, 4);

        assertThat(carrito.contarTotalUnidades()).isEqualTo(4);
        assertThat(carrito.calcularTotal()).isEqualByComparingTo(new BigDecimal("320.00"));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar cantidad con valor no positivo")
    void actualizarCantidad_valorInvalido_lanzaExcepcion() {
        CarritoItem item = CarritoItem.crear(10L, "Camiseta Real Madrid", Talla.M, new BigDecimal("80.00"), 2);
        carrito.agregarItem(item);

        assertThatThrownBy(() -> carrito.actualizarCantidad(10L, 0))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    @DisplayName("Debe quitar ítem existente del carrito")
    void quitarItem_itemExistente_eliminaDelCarrito() {
        CarritoItem item = CarritoItem.crear(10L, "Camiseta Real Madrid", Talla.M, new BigDecimal("80.00"), 2);
        carrito.agregarItem(item);

        carrito.quitarItem(10L);

        assertThat(carrito.estaVacio()).isTrue();
        assertThat(carrito.calcularTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Debe lanzar excepción al quitar ítem que no está en el carrito")
    void quitarItem_noExiste_lanzaExcepcion() {
        assertThatThrownBy(() -> carrito.quitarItem(999L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no existe en el carrito");
    }

    @Test
    @DisplayName("Debe vaciar el carrito completamente")
    void vaciar_carritoConItems_quedaVacio() {
        carrito.agregarItem(CarritoItem.crear(10L, "Camiseta 1", Talla.S, new BigDecimal("50.00"), 1));
        carrito.agregarItem(CarritoItem.crear(11L, "Camiseta 2", Talla.L, new BigDecimal("70.00"), 1));

        carrito.vaciar();

        assertThat(carrito.estaVacio()).isTrue();
        assertThat(carrito.getItems()).isEmpty();
    }
}
