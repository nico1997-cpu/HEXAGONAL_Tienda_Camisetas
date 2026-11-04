package com.camisastienda.application.usecase;

import com.camisastienda.application.port.out.OrdenRepositoryPort;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarMisOrdenesUseCaseTest {

    private static final Long USUARIO_ID = 1L;

    @Mock
    private OrdenRepositoryPort ordenRepository;

    private ListarMisOrdenesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListarMisOrdenesUseCase(ordenRepository);
    }

    @Test
    @DisplayName("Devuelve las órdenes del usuario autenticado")
    void listar_ordenesDelUsuario_retornaLista() {
        OrdenItem item = new OrdenItem(10L, 1L, 100L, "Camiseta", Talla.M, 1, new BigDecimal("80.00"));
        Orden orden = new Orden(1L, USUARIO_ID, List.of(item), new BigDecimal("80.00"), EstadoOrden.CREADA, Instant.now());

        when(ordenRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(List.of(orden));

        List<Orden> resultado = useCase.ejecutar(USUARIO_ID);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        assertThat(resultado.get(0).getUsuarioId()).isEqualTo(USUARIO_ID);
    }

    @Test
    @DisplayName("Devuelve lista vacía si el usuario no tiene órdenes")
    void listar_sinOrdenes_retornaVacio() {
        when(ordenRepository.buscarPorUsuarioId(USUARIO_ID)).thenReturn(List.of());

        List<Orden> resultado = useCase.ejecutar(USUARIO_ID);

        assertThat(resultado).isEmpty();
    }
}

