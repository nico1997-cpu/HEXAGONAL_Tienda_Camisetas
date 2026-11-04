package com.camisastienda.application.usecase;

import com.camisastienda.application.port.out.OrdenRepositoryPort;
import com.camisastienda.domain.exception.RecursoNoEncontradoException;
import com.camisastienda.domain.model.EstadoOrden;
import com.camisastienda.domain.model.Orden;
import com.camisastienda.domain.model.OrdenItem;
import com.camisastienda.domain.model.Talla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerDetalleOrdenUseCaseTest {

    private static final Long USUARIO_ID = 1L;
    private static final Long ORDEN_ID = 10L;

    @Mock
    private OrdenRepositoryPort ordenRepository;

    private VerDetalleOrdenUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new VerDetalleOrdenUseCase(ordenRepository);
    }

    @Test
    @DisplayName("Devuelve la orden si existe y pertenece al usuario")
    void verDetalle_ordenPropia_retornaOrden() {
        OrdenItem item = new OrdenItem(1L, ORDEN_ID, 100L, "Camiseta", Talla.M, 1, new BigDecimal("80.00"));
        Orden orden = new Orden(ORDEN_ID, USUARIO_ID, List.of(item), new BigDecimal("80.00"), EstadoOrden.CREADA, Instant.now());

        when(ordenRepository.buscarPorId(ORDEN_ID)).thenReturn(Optional.of(orden));

        Orden resultado = useCase.ejecutar(USUARIO_ID, ORDEN_ID);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(ORDEN_ID);
        assertThat(resultado.getUsuarioId()).isEqualTo(USUARIO_ID);
    }

    @Test
    @DisplayName("Lanza RecursoNoEncontradoException si la orden no existe")
    void verDetalle_ordenInexistente_lanza404() {
        when(ordenRepository.buscarPorId(ORDEN_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar(USUARIO_ID, ORDEN_ID))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Orden no encontrada");
    }

    @Test
    @DisplayName("Lanza RecursoNoEncontradoException si la orden pertenece a otro usuario")
    void verDetalle_ordenDeOtroUsuario_lanza404() {
        Long otroUsuarioId = 99L;
        OrdenItem item = new OrdenItem(1L, ORDEN_ID, 100L, "Camiseta", Talla.M, 1, new BigDecimal("80.00"));
        Orden ordenAjena = new Orden(ORDEN_ID, otroUsuarioId, List.of(item), new BigDecimal("80.00"), EstadoOrden.CREADA, Instant.now());

        when(ordenRepository.buscarPorId(ORDEN_ID)).thenReturn(Optional.of(ordenAjena));

        assertThatThrownBy(() -> useCase.ejecutar(USUARIO_ID, ORDEN_ID))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Orden no encontrada");
    }
}

