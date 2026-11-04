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
class VaciarCarritoUseCaseTest {

    private static final Long USUARIO_ID = 1L;

    @Mock
    private CarritoRepositoryPort carritoRepository;

    private VaciarCarritoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new VaciarCarritoUseCase(carritoRepository);
    }

    @Test
    @DisplayName("Vacía el carrito con ítems y guarda los cambios")
    void vaciar_carritoConItems_quedaVacioYGuarda() {
        Carrito carrito = Carrito.paraUsuario(USUARIO_ID);
        carrito.agregarItem(new CarritoItem(50L, null, 100L, "Camiseta Real Madrid",
                Talla.M, new BigDecimal("89.99"), 2));
        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.of(carrito));
        when(carritoRepository.guardar(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.ejecutar(USUARIO_ID);

        assertThat(carrito.estaVacio()).isTrue();
        verify(carritoRepository).guardar(any(Carrito.class));
    }

    @Test
    @DisplayName("Si el usuario no tiene carrito, la operación es un no-op")
    void vaciar_sinCarrito_noHaceNada() {
        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.empty());

        useCase.ejecutar(USUARIO_ID);

        verify(carritoRepository, never()).guardar(any(Carrito.class));
    }
}
