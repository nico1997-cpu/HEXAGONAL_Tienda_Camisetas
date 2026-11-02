package com.camisastienda.infrastructure.adapter.out.persistence;

import com.camisastienda.domain.model.Carrito;
import com.camisastienda.domain.model.CarritoItem;
import com.camisastienda.domain.model.EstadoOrden;
import com.camisastienda.domain.model.Orden;
import com.camisastienda.domain.model.OrdenItem;
import com.camisastienda.domain.model.Producto;
import com.camisastienda.domain.model.ProductoTalla;
import com.camisastienda.domain.model.Rol;
import com.camisastienda.domain.model.Talla;
import com.camisastienda.domain.model.Usuario;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.CarritoEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.OrdenEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.ProductoEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.ProductoTallaEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.mapper.CarritoPersistenceMapper;
import com.camisastienda.infrastructure.adapter.out.persistence.mapper.OrdenPersistenceMapper;
import com.camisastienda.infrastructure.adapter.out.persistence.mapper.ProductoPersistenceMapper;
import com.camisastienda.infrastructure.adapter.out.persistence.mapper.UsuarioPersistenceMapper;
import com.camisastienda.infrastructure.adapter.out.persistence.repository.CarritoJpaRepository;
import com.camisastienda.infrastructure.adapter.out.persistence.repository.OrdenJpaRepository;
import com.camisastienda.infrastructure.adapter.out.persistence.repository.ProductoJpaRepository;
import com.camisastienda.infrastructure.adapter.out.persistence.repository.ProductoTallaJpaRepository;
import com.camisastienda.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersistenceAdaptersTest {

    @Mock
    private UsuarioJpaRepository usuarioRepo;
    @Mock
    private ProductoJpaRepository productoRepo;
    @Mock
    private ProductoTallaJpaRepository tallaRepo;
    @Mock
    private CarritoJpaRepository carritoRepo;
    @Mock
    private OrdenJpaRepository ordenRepo;

    @Test
    @DisplayName("UsuarioPersistenceAdapter guarda y busca por email")
    void usuarioAdapter_guardaYBusca() {
        UsuarioPersistenceAdapter adapter = new UsuarioPersistenceAdapter(usuarioRepo, new UsuarioPersistenceMapper());
        UsuarioEntity entity = new UsuarioEntity(1L, "Pedro", "pedro@test.com", "hash", "CLIENTE", Instant.now());

        when(usuarioRepo.findByEmailIgnoreCase("pedro@test.com")).thenReturn(Optional.of(entity));
        when(usuarioRepo.save(any(UsuarioEntity.class))).thenReturn(entity);

        Optional<Usuario> encontrado = adapter.buscarPorEmail("pedro@test.com");
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Pedro");

        Usuario guardado = adapter.guardar(encontrado.get());
        assertThat(guardado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("ProductoPersistenceAdapter filtra y guarda productos y tallas")
    void productoAdapter_filtraYGuarda() {
        ProductoPersistenceAdapter adapter = new ProductoPersistenceAdapter(
                productoRepo, tallaRepo, new ProductoPersistenceMapper());

        ProductoEntity prodEntity = new ProductoEntity(1L, "Camiseta City", "Desc", "Manchester City",
                "Premier League", "2024", new BigDecimal("95.00"), null, true, Instant.now());
        ProductoTallaEntity tallaEntity = new ProductoTallaEntity(10L, prodEntity, "M", 12, 0L);
        prodEntity.agregarTalla(tallaEntity);

        when(productoRepo.buscarConFiltros("City", null, null, true)).thenReturn(List.of(prodEntity));
        when(productoRepo.findById(1L)).thenReturn(Optional.of(prodEntity));
        when(tallaRepo.saveAndFlush(any(ProductoTallaEntity.class))).thenReturn(tallaEntity);

        List<Producto> filtrados = adapter.buscarConFiltros("City", null, null, true);
        assertThat(filtrados).hasSize(1);
        assertThat(filtrados.get(0).getEquipo()).isEqualTo("Manchester City");

        ProductoTalla ptGuardada = adapter.guardarTalla(new ProductoTalla(10L, 1L, Talla.M, 12, 0L));
        assertThat(ptGuardada.getStock()).isEqualTo(12);
    }

    @Test
    @DisplayName("CarritoPersistenceAdapter guarda carrito con sus ítems")
    void carritoAdapter_guardaCorrectamente() {
        CarritoPersistenceAdapter adapter = new CarritoPersistenceAdapter(
                carritoRepo, tallaRepo, new CarritoPersistenceMapper());

        ProductoEntity prod = new ProductoEntity(1L, "Camiseta", "Desc", "Equipo", "Liga",
                "2024", new BigDecimal("80.00"), null, true, Instant.now());
        ProductoTallaEntity pt = new ProductoTallaEntity(5L, prod, "S", 10, 0L);
        CarritoEntity carritoGuardado = new CarritoEntity(1L, 2L, Instant.now());

        when(carritoRepo.findById(1L)).thenReturn(Optional.of(carritoGuardado));
        when(tallaRepo.findById(5L)).thenReturn(Optional.of(pt));
        when(carritoRepo.save(any(CarritoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        Carrito carrito = new Carrito(1L, 2L,
                List.of(CarritoItem.crear(5L, "Camiseta", Talla.S, new BigDecimal("80.00"), 3)), Instant.now());

        Carrito resultado = adapter.guardar(carrito);
        assertThat(resultado.getItems()).hasSize(1);
        assertThat(resultado.contarTotalUnidades()).isEqualTo(3);

        adapter.eliminarPorUsuarioId(2L);
        verify(carritoRepo).deleteByUsuarioId(2L);
    }

    @Test
    @DisplayName("OrdenPersistenceAdapter guarda y consulta órdenes por usuario")
    void ordenAdapter_guardaYConsulta() {
        OrdenPersistenceAdapter adapter = new OrdenPersistenceAdapter(
                ordenRepo, tallaRepo, new OrdenPersistenceMapper());

        ProductoEntity prod = new ProductoEntity(1L, "Camiseta", "Desc", "Equipo", "Liga",
                "2024", new BigDecimal("80.00"), null, true, Instant.now());
        ProductoTallaEntity pt = new ProductoTallaEntity(5L, prod, "L", 10, 0L);

        when(tallaRepo.findById(5L)).thenReturn(Optional.of(pt));
        when(ordenRepo.save(any(OrdenEntity.class))).thenAnswer(inv -> {
            OrdenEntity e = inv.getArgument(0);
            e.setId(99L);
            return e;
        });

        Orden orden = new Orden(null, 2L,
                List.of(new OrdenItem(null, null, 5L, "Camiseta", Talla.L, 2, new BigDecimal("80.00"))),
                new BigDecimal("160.00"), EstadoOrden.CREADA, Instant.now());

        Orden guardada = adapter.guardar(orden);
        assertThat(guardada.getId()).isEqualTo(99L);
        assertThat(guardada.getItems()).hasSize(1);
    }
}
