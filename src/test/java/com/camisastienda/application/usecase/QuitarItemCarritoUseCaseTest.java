package com.camisastienda.application.usecase;

import com.camisastienda.application.port.out.CarritoRepositoryPort;
import com.camisastienda.domain.exception.RecursoNoEncontradoException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuitarItemCarritoUseCaseTest {

    private static final Long USUARIO_ID = 1L;
    private static final Long ITEM_ID = 50L;

    @Mock
    private CarritoRepositoryPort carritoRepository;

    private QuitarItemCarritoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new QuitarItemCarritoUseCase(carritoRepository);
    }

    private Carrito carritoConItem() {
        Carrito carrito = Carrito.paraUsuario(USUARIO_ID);
        carrito.agregarItem(new CarritoItem(ITEM_ID, null, 100L, "Camiseta Real Madrid",
                Talla.M, new BigDecimal("89.99"), 2));
        return carrito;
    }

    @Test
    @DisplayName("Quita el ítem del carrito y guarda los cambios")
    void quitar_itemExistente_eliminaYGuarda() {
        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.of(carritoConItem()));
        when(carritoRepository.guardar(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        Carrito resultado = useCase.ejecutar(USUARIO_ID, ITEM_ID);

        assertThat(resultado.estaVacio()).isTrue();
        assertThat(resultado.calcularTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(carritoRepository).guardar(any(Carrito.class));
    }

    @Test
    @DisplayName("Lanza 404 si el ítem no existe en el carrito")
    void quitar_itemInexistente_lanza404() {
        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.of(carritoConItem()));

        assertThatThrownBy(() -> useCase.ejecutar(USUARIO_ID, 999L))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(carritoRepository, never()).guardar(any(Carrito.class));
    }

    @Test
    @DisplayName("Lanza 404 si el usuario no tiene carrito")
    void quitar_sinCarrito_lanza404() {
        when(carritoRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar(USUARIO_ID, ITEM_ID))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
