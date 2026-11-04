package com.camisastienda.application.usecase;

import com.camisastienda.application.port.out.CarritoRepositoryPort;
import com.camisastienda.application.port.out.ProductoRepositoryPort;
import com.camisastienda.domain.exception.ProductoNoEncontradoException;
import com.camisastienda.domain.exception.RecursoNoEncontradoException;
import com.camisastienda.domain.exception.ReglaNegocioException;
import com.camisastienda.domain.exception.StockInsuficienteException;
import com.camisastienda.domain.model.Carrito;
import com.camisastienda.domain.model.CarritoItem;
import com.camisastienda.domain.model.Producto;
import com.camisastienda.domain.model.ProductoTalla;
import com.camisastienda.domain.model.Talla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarItemCarritoUseCaseTest {

    private static final Long USUARIO_ID = 1L;
    private static final Long PRODUCTO_ID = 10L;
    private static final Long PRODUCTO_TALLA_ID = 100L;
    private static final BigDecimal PRECIO = new BigDecimal("89.99");

    @Mock
    private CarritoRepositoryPort carritoRepository;
    @Mock
    private ProductoRepositoryPort productoRepository;

    private AgregarItemCarritoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarItemCarritoUseCase(carritoRepository, productoRepository);
    }

    // Helper: producto con una talla y stock determinado
    private Producto productoConTalla(Talla talla, int stock) {
        Producto producto = new Producto(PRODUCTO_ID, "Camiseta Real Madrid", "Descripción",
                "Real Madrid", "LaLiga", "2024/2025", PRECIO, null, true, new ArrayList<>(), Instant.now());
        producto.agregarTalla(new ProductoTalla(PRODUCTO_TALLA_ID, PRODUCTO_ID, talla, stock, 0L));
        return producto;
    }

    // Helper: el adaptador devuelve el mismo carrito "guardado"
    private void guardarDevuelveElMismoCarrito() {
        when(carritoRepository.guardar(any(Carrito.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("Agrega un ítem nuevo a un carrito que no existía y lo crea")
    void agregar_itemNuevo_creaCarritoYLoGuarda() {
        when(productoRepository.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.of(productoConTalla(Talla.M, 20)));
        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.empty());
        guardarDevuelveElMismoCarrito();

        Carrito resultado = useCase.ejecutar(USUARIO_ID, PRODUCTO_ID, "M", 2);

        assertThat(resultado.getItems()).hasSize(1);
        CarritoItem item = resultado.getItems().get(0);
        assertThat(item.getProductoTallaId()).isEqualTo(PRODUCTO_TALLA_ID);
        assertThat(item.getCantidad()).isEqualTo(2);
        assertThat(item.getPrecioUnitario()).isEqualByComparingTo(PRECIO);
        assertThat(resultado.calcularTotal()).isEqualByComparingTo(new BigDecimal("179.98"));
        verify(carritoRepository).guardar(any(Carrito.class));
    }

    @Test
    @DisplayName("Si el mismo producto+talla ya existe, suma la cantidad en una sola línea")
    void agregar_itemDuplicado_sumaCantidad() {
        Carrito carrito = Carrito.paraUsuario(USUARIO_ID);
        carrito.agregarItem(CarritoItem.crear(PRODUCTO_TALLA_ID, "Camiseta Real Madrid", Talla.M, PRECIO, 2));
        when(productoRepository.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.of(productoConTalla(Talla.M, 20)));
        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.of(carrito));
        guardarDevuelveElMismoCarrito();

        Carrito resultado = useCase.ejecutar(USUARIO_ID, PRODUCTO_ID, "M", 3);

        assertThat(resultado.getItems()).hasSize(1);
        assertThat(resultado.getItems().get(0).getCantidad()).isEqualTo(5);
        assertThat(resultado.calcularTotal()).isEqualByComparingTo(new BigDecimal("449.95"));
    }

    @Test
    @DisplayName("No permite superar el stock disponible sumando lo que ya está en el carrito")
    void agregar_superaStock_lanzaStockInsuficiente() {
        Carrito carrito = Carrito.paraUsuario(USUARIO_ID);
        carrito.agregarItem(CarritoItem.crear(PRODUCTO_TALLA_ID, "Camiseta Real Madrid", Talla.M, PRECIO, 4));
        when(productoRepository.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.of(productoConTalla(Talla.M, 5)));
        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.of(carrito));

        assertThatThrownBy(() -> useCase.ejecutar(USUARIO_ID, PRODUCTO_ID, "M", 2))
                .isInstanceOf(StockInsuficienteException.class)
                .hasMessageContaining("Stock insuficiente");

        verify(carritoRepository, never()).guardar(any(Carrito.class));
    }

    @Test
    @DisplayName("Lanza 404 si el producto no existe")
    void agregar_productoNoEncontrado_lanzaExcepcion() {
        when(productoRepository.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar(USUARIO_ID, PRODUCTO_ID, "M", 1))
                .isInstanceOf(ProductoNoEncontradoException.class);

        verify(carritoRepository, never()).guardar(any(Carrito.class));
    }

    @Test
    @DisplayName("Lanza 404 si el producto no tiene la talla solicitada")
    void agregar_tallaInexistente_lanzaExcepcion() {
        when(productoRepository.buscarPorId(PRODUCTO_ID)).thenReturn(Optional.of(productoConTalla(Talla.M, 20)));

        assertThatThrownBy(() -> useCase.ejecutar(USUARIO_ID, PRODUCTO_ID, "XXL", 1))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("XXL");
    }

    @Test
    @DisplayName("Lanza excepción de negocio si la talla no es válida")
    void agregar_tallaInvalida_lanzaReglaNegocio() {
        assertThatThrownBy(() -> useCase.ejecutar(USUARIO_ID, PRODUCTO_ID, "ZZ", 1))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    @DisplayName("Lanza excepción de negocio si la cantidad no es positiva")
    void agregar_cantidadInvalida_lanzaReglaNegocio() {
        assertThatThrownBy(() -> useCase.ejecutar(USUARIO_ID, PRODUCTO_ID, "M", 0))
                .isInstanceOf(ReglaNegocioException.class);

        assertThatThrownBy(() -> useCase.ejecutar(USUARIO_ID, PRODUCTO_ID, "M", -1))
                .isInstanceOf(ReglaNegocioException.class);

        verify(carritoRepository, never()).guardar(any(Carrito.class));
        verify(productoRepository, never()).buscarPorId(anyLong());
    }
}
