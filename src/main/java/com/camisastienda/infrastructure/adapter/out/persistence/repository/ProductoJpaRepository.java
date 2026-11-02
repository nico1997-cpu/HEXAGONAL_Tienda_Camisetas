package com.camisastienda.infrastructure.adapter.out.persistence.repository;

import com.camisastienda.infrastructure.adapter.out.persistence.entity.ProductoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoJpaRepository extends JpaRepository<ProductoEntity, Long> {

    List<ProductoEntity> findByActivoTrue();

    @Query("""
            SELECT p FROM ProductoEntity p
            WHERE (:soloActivos IS NULL OR :soloActivos = false OR p.activo = true)
              AND (:equipo IS NULL OR LOWER(p.equipo) LIKE LOWER(CONCAT('%', :equipo, '%')))
              AND (:liga IS NULL OR LOWER(p.liga) LIKE LOWER(CONCAT('%', :liga, '%')))
              AND (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
            """)
    List<ProductoEntity> buscarConFiltros(
            @Param("equipo") String equipo,
            @Param("liga") String liga,
            @Param("nombre") String nombre,
            @Param("soloActivos") Boolean soloActivos
    );

    @Query("""
            SELECT p FROM ProductoEntity p
            WHERE (:soloActivos IS NULL OR :soloActivos = false OR p.activo = true)
              AND (:equipo IS NULL OR LOWER(p.equipo) LIKE LOWER(CONCAT('%', :equipo, '%')))
              AND (:liga IS NULL OR LOWER(p.liga) LIKE LOWER(CONCAT('%', :liga, '%')))
              AND (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
            """)
    Page<ProductoEntity> buscarConFiltrosPaginado(
            @Param("equipo") String equipo,
            @Param("liga") String liga,
            @Param("nombre") String nombre,
            @Param("soloActivos") Boolean soloActivos,
            Pageable pageable
    );
}
