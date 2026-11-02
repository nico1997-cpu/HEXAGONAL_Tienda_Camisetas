package com.camisastienda.infrastructure.adapter.out.persistence;

import com.camisastienda.TestcontainersConfiguration;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.ProductoEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.ProductoTallaEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.repository.ProductoJpaRepository;
import com.camisastienda.infrastructure.adapter.out.persistence.repository.ProductoTallaJpaRepository;
import com.camisastienda.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class PersistenciaJpaIT {

    @Autowired
    private UsuarioJpaRepository usuarioRepo;

    @Autowired
    private ProductoJpaRepository productoRepo;

    @Autowired
    private ProductoTallaJpaRepository tallaRepo;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Flyway carga datos iniciales y repositorios consultan correctamente")
    void flywayYRepositorios_consultanDatosSemilla() {
        assertThat(usuarioRepo.findByEmailIgnoreCase("admin@camisastienda.com")).isPresent();
        assertThat(productoRepo.findByActivoTrue()).hasSizeGreaterThanOrEqualTo(5);
    }

    @Test
    @DisplayName("Consulta filtrada y paginada por equipo y liga funciona en MySQL")
    void buscarConFiltrosPaginado_filtraPorLigaYEquipo() {
        Page<ProductoEntity> pagina = productoRepo.buscarConFiltrosPaginado(
                null, "LaLiga", null, true, PageRequest.of(0, 10));
        assertThat(pagina.getTotalElements()).isGreaterThanOrEqualTo(2);

        List<ProductoTallaEntity> tallasConStock = tallaRepo.findByProductoIdAndStockGreaterThan(1L, 0);
        assertThat(tallasConStock).isNotEmpty();
    }

    @Test
    @DisplayName("Bloqueo optimista (@Version) lanza excepción ante modificación concurrente")
    void bloqueoOptimista_modificacionConcurrente_lanzaExcepcion() {
        ProductoTallaEntity copia1 = tallaRepo.findById(1L).orElseThrow();
        entityManager.detach(copia1);

        ProductoTallaEntity copia2 = tallaRepo.findById(1L).orElseThrow();
        entityManager.detach(copia2);

        copia1.setStock(copia1.getStock() - 1);
        tallaRepo.saveAndFlush(copia1);

        copia2.setStock(copia2.getStock() - 1);
        assertThatThrownBy(() -> tallaRepo.saveAndFlush(copia2))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }
}
