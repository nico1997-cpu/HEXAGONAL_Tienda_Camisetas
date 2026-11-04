package com.camisastienda.application.usecase;

import com.camisastienda.application.port.out.CarritoRepositoryPort;
import com.camisastienda.domain.model.Carrito;
import com.camisastienda.domain.model.CarritoItem;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerCarritoUseCaseTest {

    private static final Long USUARIO_ID = 1L;

    @Mock
    private CarritoRepositoryPort carritoRepository;

    private VerCarritoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new VerCarritoUseCase(carritoRepository);
    }

    @Test
    @DisplayName("Devuelve el carrito existente del usuario con su total")
    void ver_carritoExistente_loDevuelveConTotal() {
        Carrito carrito = Carrito.paraUsuario(USUARIO_ID);
        carrito.agregarItem(new CarritoItem(50L, null, 100L, "Camiseta Real Madrid",
                Talla.M, new BigDecimal("89.99"), 2));
        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.of(carrito));

        Carrito resultado = useCase.ejecutar(USUARIO_ID);

        assertThat(resultado.getUsuarioId()).isEqualTo(USUARIO_ID);
        assertThat(resultado.getItems()).hasSize(1);
        assertThat(resultado.calcularTotal()).isEqualByComparingTo(new BigDecimal("179.98"));
        assertThat(resultado.contarTotalUnidades()).isEqualTo(2);
    }

    @Test
    @DisplayName("Si el usuario no tiene carrito, devuelve uno vacío sin persistirlo")
    void ver_sinCarrito_devuelveCarritoVacio() {
        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.empty());

        Carrito resultado = useCase.ejecutar(USUARIO_ID);

        assertThat(resultado.getUsuarioId()).isEqualTo(USUARIO_ID);
        assertThat(resultado.estaVacio()).isTrue();
        assertThat(resultado.calcularTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(carritoRepository, never()).guardar(any(Carrito.class));
    }
}
