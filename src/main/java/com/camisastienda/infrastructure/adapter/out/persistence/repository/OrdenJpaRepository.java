package com.camisastienda.infrastructure.adapter.out.persistence.repository;

import com.camisastienda.infrastructure.adapter.out.persistence.entity.OrdenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Repositorio Spring Data JPA para consultar órdenes de compra por usuario. */
public interface OrdenJpaRepository extends JpaRepository<OrdenEntity, Long> {
    List<OrdenEntity> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);
}
