package com.camisastienda.infrastructure.adapter.out.persistence.mapper;

import com.camisastienda.domain.model.Carrito;
import com.camisastienda.domain.model.EstadoOrden;
import com.camisastienda.domain.model.Orden;
import com.camisastienda.domain.model.Producto;
import com.camisastienda.domain.model.ProductoTalla;
import com.camisastienda.domain.model.Rol;
import com.camisastienda.domain.model.Talla;
import com.camisastienda.domain.model.Usuario;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.CarritoEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.CarritoItemEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.OrdenEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.OrdenItemEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.ProductoEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.ProductoTallaEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceMappersTest {

    private final UsuarioPersistenceMapper usuarioMapper = new UsuarioPersistenceMapper();
    private final ProductoPersistenceMapper productoMapper = new ProductoPersistenceMapper();
    private final CarritoPersistenceMapper carritoMapper = new CarritoPersistenceMapper();
    private final OrdenPersistenceMapper ordenMapper = new OrdenPersistenceMapper();

    @Test
    @DisplayName("UsuarioPersistenceMapper convierte ida y vuelta entre Dominio y Entidad")
    void usuarioMapper_convierteCorrectamente() {
        Usuario domain = new Usuario(1L, "Ana", "ana@test.com", "hash123", Rol.ADMIN, Instant.now());
        UsuarioEntity entity = usuarioMapper.toEntity(domain);
        Usuario reconvertido = usuarioMapper.toDomain(entity);

        assertThat(reconvertido.getId()).isEqualTo(1L);
        assertThat(reconvertido.getNombre()).isEqualTo("Ana");
        assertThat(reconvertido.getEmail()).isEqualTo("ana@test.com");
        assertThat(reconvertido.getRol()).isEqualTo(Rol.ADMIN);
        assertThat(usuarioMapper.toDomain(null)).isNull();
        assertThat(usuarioMapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("ProductoPersistenceMapper convierte Producto y sus Tallas correctamente")
    void productoMapper_convierteCorrectamente() {
        ProductoTalla pt = new ProductoTalla(10L, 1L, Talla.M, 15, 2L);
        Producto domain = new Producto(1L, "Camiseta RM", "Local", "Real Madrid", "LaLiga",
                "2024/2025", new BigDecimal("89.99"), "http://img", true, List.of(pt), Instant.now());

        ProductoEntity entity = productoMapper.toEntity(domain);
        assertThat(entity.getTallas()).hasSize(1);
        assertThat(entity.getTallas().get(0).getProducto()).isSameAs(entity);

        Producto reconvertido = productoMapper.toDomain(entity);
        assertThat(reconvertido.getId()).isEqualTo(1L);
        assertThat(reconvertido.getTallas()).hasSize(1);
        assertThat(reconvertido.getTallas().get(0).getTalla()).isEqualTo(Talla.M);
        assertThat(reconvertido.getTallas().get(0).getStock()).isEqualTo(15);
    }

    @Test
    @DisplayName("CarritoPersistenceMapper convierte CarritoEntity con sus ítems a Dominio")
    void carritoMapper_convierteCorrectamente() {
        ProductoEntity prod = new ProductoEntity(1L, "Camiseta Barça", "Desc", "Barça", "LaLiga",
                "2024", new BigDecimal("90.00"), null, true, Instant.now());
        ProductoTallaEntity pt = new ProductoTallaEntity(5L, prod, "L", 10, 0L);
        CarritoEntity carritoEntity = new CarritoEntity(2L, 99L, Instant.now());
        carritoEntity.agregarItem(new CarritoItemEntity(7L, carritoEntity, pt, 2));

        Carrito domain = carritoMapper.toDomain(carritoEntity);

        assertThat(domain.getId()).isEqualTo(2L);
        assertThat(domain.getUsuarioId()).isEqualTo(99L);
        assertThat(domain.getItems()).hasSize(1);
        assertThat(domain.calcularTotal()).isEqualByComparingTo(new BigDecimal("180.00"));
    }

    @Test
    @DisplayName("OrdenPersistenceMapper convierte OrdenEntity con sus ítems a Dominio")
    void ordenMapper_convierteCorrectamente() {
        ProductoEntity prod = new ProductoEntity(1L, "Camiseta Colombia", "Desc", "Colombia", "Selecciones",
                "2024", new BigDecimal("100.00"), null, true, Instant.now());
        ProductoTallaEntity pt = new ProductoTallaEntity(8L, prod, "S", 5, 0L);
        OrdenEntity ordenEntity = new OrdenEntity(3L, 99L, new BigDecimal("200.00"), "CREADA", Instant.now());
        ordenEntity.agregarItem(new OrdenItemEntity(12L, ordenEntity, pt, 2, new BigDecimal("100.00")));

        Orden domain = ordenMapper.toDomain(ordenEntity);

        assertThat(domain.getId()).isEqualTo(3L);
        assertThat(domain.getEstado()).isEqualTo(EstadoOrden.CREADA);
        assertThat(domain.getItems()).hasSize(1);
        assertThat(domain.getTotal()).isEqualByComparingTo(new BigDecimal("200.00"));
    }
}
