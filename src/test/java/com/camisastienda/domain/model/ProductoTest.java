package com.camisastienda.domain.model;

import com.camisastienda.domain.exception.ReglaNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductoTest {

    @Test
    @DisplayName("Debe crear producto válido correctamente")
    void crearProducto_datosValidos_creaExitosamente() {
        Producto p = Producto.nuevo("Camiseta Real Madrid", "Local 24/25", "Real Madrid", "LaLiga",
                "2024/2025", new BigDecimal("89.99"), "http://img.png");

        assertThat(p.getNombre()).isEqualTo("Camiseta Real Madrid");
        assertThat(p.isActivo()).isTrue();
        assertThat(p.getTallas()).isEmpty();
    }

    @Test
    @DisplayName("Debe fallar al crear producto con nombre vacío o precio negativo")
    void crearProducto_datosInvalidos_lanzaExcepcion() {
        assertThatThrownBy(() -> Producto.nuevo("", "desc", "Real Madrid", "LaLiga", "2024", BigDecimal.TEN, null))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("nombre");

        assertThatThrownBy(() -> Producto.nuevo("Camiseta", "desc", "Real Madrid", "LaLiga", "2024", new BigDecimal("-5"), null))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("precio");
    }

    @Test
    @DisplayName("Debe agregar tallas y buscarlas")
    void agregarTalla_tallaValida_agregaCorrectamente() {
        Producto p = Producto.nuevo("Camiseta Arsenal", "Local", "Arsenal", "Premier", "2024", BigDecimal.valueOf(90), null);
        ProductoTalla pt = ProductoTalla.nueva(p.getId(), Talla.M, 10);
        p.agregarTalla(pt);

        assertThat(p.getTallas()).hasSize(1);
        assertThat(p.buscarTalla(Talla.M)).isPresent();
        assertThat(p.buscarTalla(Talla.S)).isEmpty();
    }

    @Test
    @DisplayName("Debe rechazar agregar la misma talla dos veces")
    void agregarTalla_tallaDuplicada_lanzaExcepcion() {
        Producto p = Producto.nuevo("Camiseta Arsenal", "Local", "Arsenal", "Premier", "2024", BigDecimal.valueOf(90), null);
        p.agregarTalla(ProductoTalla.nueva(p.getId(), Talla.L, 5));

        assertThatThrownBy(() -> p.agregarTalla(ProductoTalla.nueva(p.getId(), Talla.L, 10)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ya está registrada");
    }

    @Test
    @DisplayName("tieneStockDisponible debe responder según el estado y stock")
    void tieneStockDisponible_respondeSegunEstado() {
        Producto p = Producto.nuevo("Camiseta Barça", "Local", "Barcelona", "LaLiga", "2024", BigDecimal.valueOf(90), null);
        assertThat(p.tieneStockDisponible()).isFalse();

        p.agregarTalla(ProductoTalla.nueva(p.getId(), Talla.M, 3));
        assertThat(p.tieneStockDisponible()).isTrue();

        p.desactivar();
        assertThat(p.tieneStockDisponible()).isFalse();
    }
}
