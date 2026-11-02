package com.camisastienda.infrastructure.adapter.out.persistence.repository;

import com.camisastienda.infrastructure.adapter.out.persistence.entity.CarritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoJpaRepository extends JpaRepository<CarritoEntity, Long> {
    Optional<CarritoEntity> findByUsuarioId(Long usuarioId);
    void deleteByUsuarioId(Long usuarioId);
}
