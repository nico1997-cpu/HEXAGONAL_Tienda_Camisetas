package com.camisastienda.infrastructure.adapter.out.persistence.repository;

import com.camisastienda.infrastructure.adapter.out.persistence.entity.OrdenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenJpaRepository extends JpaRepository<OrdenEntity, Long> {
    List<OrdenEntity> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);
}
