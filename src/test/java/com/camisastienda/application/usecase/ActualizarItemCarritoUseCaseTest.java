package com.camisastienda.application.usecase;

import com.camisastienda.application.port.out.CarritoRepositoryPort;
import com.camisastienda.application.port.out.ProductoRepositoryPort;
import com.camisastienda.domain.exception.RecursoNoEncontradoException;
import com.camisastienda.domain.exception.StockInsuficienteException;
import com.camisastienda.domain.model.Carrito;
import com.camisastienda.domain.model.CarritoItem;
import com.camisastienda.domain.model.ProductoTalla;
import com.camisastienda.domain.model.Talla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarItemCarritoUseCaseTest {

    private static final Long USUARIO_ID = 1L;
    private static final Long ITEM_ID = 50L;
    private static final Long PRODUCTO_TALLA_ID = 100L;

    @Mock
    private CarritoRepositoryPort carritoRepository;
    @Mock
    private ProductoRepositoryPort productoRepository;

    private ActualizarItemCarritoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ActualizarItemCarritoUseCase(carritoRepository, productoRepository);
    }

    // Helper: carrito con un ítem guardado (con ID) de 2 unidades
    private Carrito carritoConItem() {
        Carrito carrito = Carrito.paraUsuario(USUARIO_ID);
        carrito.agregarItem(new CarritoItem(ITEM_ID, null, PRODUCTO_TALLA_ID, "Camiseta Real Madrid",
                Talla.M, new BigDecimal("89.99"), 2));
        return carrito;
    }

    @Test
    @DisplayName("Actualiza la cantidad del ítem correctamente")
    void actualizar_cantidadValida_reemplazaCantidad() {
        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.of(carritoConItem()));
        when(productoRepository.buscarTallaPorId(PRODUCTO_TALLA_ID))
                .thenReturn(Optional.of(new ProductoTalla(PRODUCTO_TALLA_ID, 10L, Talla.M, 20, 0L)));
        when(carritoRepository.guardar(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        Carrito resultado = useCase.ejecutar(USUARIO_ID, ITEM_ID, 5);

        assertThat(resultado.getItems()).hasSize(1);
        assertThat(resultado.getItems().get(0).getCantidad()).isEqualTo(5);
        assertThat(resultado.calcularTotal()).isEqualByComparingTo(new BigDecimal("449.95"));
    }

    @Test
    @DisplayName("No permite actualizar a una cantidad mayor al stock disponible")
    void actualizar_superaStock_lanzaStockInsuficiente() {
        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.of(carritoConItem()));
        when(productoRepository.buscarTallaPorId(PRODUCTO_TALLA_ID))
                .thenReturn(Optional.of(new ProductoTalla(PRODUCTO_TALLA_ID, 10L, Talla.M, 4, 0L)));

        assertThatThrownBy(() -> useCase.ejecutar(USUARIO_ID, ITEM_ID, 5))
                .isInstanceOf(StockInsuficienteException.class)
                .hasMessageContaining("Stock insuficiente");

        verify(carritoRepository, never()).guardar(any(Carrito.class));
    }

    @Test
    @DisplayName("Lanza 404 si el ítem no existe en el carrito")
    void actualizar_itemInexistente_lanza404() {
        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.of(carritoConItem()));

        assertThatThrownBy(() -> useCase.ejecutar(USUARIO_ID, 999L, 3))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("999");

        verify(carritoRepository, never()).guardar(any(Carrito.class));
    }

    @Test
    @DisplayName("Lanza 404 si el usuario no tiene carrito")
    void actualizar_sinCarrito_lanza404() {
        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar(USUARIO_ID, ITEM_ID, 3))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("Lanza 404 si la talla del producto ya no existe")
    void actualizar_tallaEliminada_lanza404() {
        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.of(carritoConItem()));
        when(productoRepository.buscarTallaPorId(PRODUCTO_TALLA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar(USUARIO_ID, ITEM_ID, 3))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
