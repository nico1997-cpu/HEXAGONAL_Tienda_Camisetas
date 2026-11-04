package com.camisastienda.application.usecase;

import com.camisastienda.application.port.out.CarritoRepositoryPort;
import com.camisastienda.application.port.out.OrdenRepositoryPort;
import com.camisastienda.application.port.out.ProductoRepositoryPort;
import com.camisastienda.domain.exception.CarritoVacioException;
import com.camisastienda.domain.exception.RecursoNoEncontradoException;
import com.camisastienda.domain.exception.StockInsuficienteException;
import com.camisastienda.domain.model.Carrito;
import com.camisastienda.domain.model.CarritoItem;
import com.camisastienda.domain.model.EstadoOrden;
import com.camisastienda.domain.model.Orden;
import com.camisastienda.domain.model.ProductoTalla;
import com.camisastienda.domain.model.Talla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckoutUseCaseTest {

    private static final Long USUARIO_ID = 1L;
    private static final Long PRODUCTO_TALLA_ID = 100L;
    private static final BigDecimal PRECIO = new BigDecimal("89.99");

    @Mock
    private CarritoRepositoryPort carritoRepository;

    @Mock
    private ProductoRepositoryPort productoRepository;

    @Mock
    private OrdenRepositoryPort ordenRepository;

    private CheckoutUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CheckoutUseCase(carritoRepository, productoRepository, ordenRepository);
    }

    private Carrito crearCarritoConItem(int cantidad) {
        Carrito carrito = Carrito.paraUsuario(USUARIO_ID);
        carrito.setId(10L);
        carrito.agregarItem(new CarritoItem(50L, 10L, PRODUCTO_TALLA_ID, "Camiseta Real Madrid",
                Talla.M, PRECIO, cantidad));
        return carrito;
    }

    @Test
    @DisplayName("Checkout exitoso: descuenta stock, crea orden con precios congelados y vacía carrito")
    void checkout_exitoso_creaOrdenDescuentaStockYVaciaCarrito() {
        Carrito carrito = crearCarritoConItem(2);
        ProductoTalla pt = new ProductoTalla(PRODUCTO_TALLA_ID, 10L, Talla.M, 10, 0L);

        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.of(carrito));
        when(productoRepository.buscarTallaPorId(PRODUCTO_TALLA_ID)).thenReturn(Optional.of(pt));
        when(productoRepository.guardarTalla(any(ProductoTalla.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ordenRepository.guardar(any(Orden.class))).thenAnswer(inv -> {
            Orden o = inv.getArgument(0);
            o.setId(999L);
            return o;
        });
        when(carritoRepository.guardar(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        Orden resultado = useCase.ejecutar(USUARIO_ID);

        // 1. Verifica orden creada
        assertThat(resultado.getId()).isEqualTo(999L);
        assertThat(resultado.getUsuarioId()).isEqualTo(USUARIO_ID);
        assertThat(resultado.getEstado()).isEqualTo(EstadoOrden.CREADA);
        assertThat(resultado.getTotal()).isEqualByComparingTo(new BigDecimal("179.98"));
        assertThat(resultado.getItems()).hasSize(1);
        assertThat(resultado.getItems().get(0).getPrecioUnitario()).isEqualByComparingTo(PRECIO);

        // 2. Verifica stock descontado (10 - 2 = 8)
        ArgumentCaptor<ProductoTalla> captorTalla = ArgumentCaptor.forClass(ProductoTalla.class);
        verify(productoRepository).guardarTalla(captorTalla.capture());
        assertThat(captorTalla.getValue().getStock()).isEqualTo(8);

        // 3. Verifica carrito vaciado y guardado
        assertThat(carrito.estaVacio()).isTrue();
        verify(carritoRepository).guardar(carrito);
    }

    @Test
    @DisplayName("Lanza CarritoVacioException si el usuario no tiene carrito")
    void checkout_sinCarrito_lanzaCarritoVacioException() {
        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar(USUARIO_ID))
                .isInstanceOf(CarritoVacioException.class);

        verify(ordenRepository, never()).guardar(any());
        verify(productoRepository, never()).guardarTalla(any());
    }

    @Test
    @DisplayName("Lanza CarritoVacioException si el carrito no tiene ítems")
    void checkout_carritoVacio_lanzaCarritoVacioException() {
        Carrito carritoVacio = Carrito.paraUsuario(USUARIO_ID);
        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.of(carritoVacio));

        assertThatThrownBy(() -> useCase.ejecutar(USUARIO_ID))
                .isInstanceOf(CarritoVacioException.class);

        verify(ordenRepository, never()).guardar(any());
        verify(productoRepository, never()).guardarTalla(any());
    }

    @Test
    @DisplayName("Lanza StockInsuficienteException y no persiste orden si la cantidad supera el inventario")
    void checkout_stockInsuficiente_lanzaExcepcion() {
        Carrito carrito = crearCarritoConItem(5);
        ProductoTalla pt = new ProductoTalla(PRODUCTO_TALLA_ID, 10L, Talla.M, 3, 0L); // solo 3 disponibles

        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.of(carrito));
        when(productoRepository.buscarTallaPorId(PRODUCTO_TALLA_ID)).thenReturn(Optional.of(pt));

        assertThatThrownBy(() -> useCase.ejecutar(USUARIO_ID))
                .isInstanceOf(StockInsuficienteException.class);

        verify(ordenRepository, never()).guardar(any());
        verify(productoRepository, never()).guardarTalla(any());
    }

    @Test
    @DisplayName("Lanza RecursoNoEncontradoException si la talla no existe en BD")
    void checkout_tallaNoEncontrada_lanzaExcepcion() {
        Carrito carrito = crearCarritoConItem(1);
        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.of(carrito));
        when(productoRepository.buscarTallaPorId(PRODUCTO_TALLA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar(USUARIO_ID))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(ordenRepository, never()).guardar(any());
        verify(productoRepository, never()).guardarTalla(any());
    }
}

