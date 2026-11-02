package com.camisastienda.infrastructure.adapter.out.persistence.repository;

import com.camisastienda.infrastructure.adapter.out.persistence.entity.ProductoTallaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoTallaJpaRepository extends JpaRepository<ProductoTallaEntity, Long> {
    Optional<ProductoTallaEntity> findByProductoIdAndTallaIgnoreCase(Long productoId, String talla);
    List<ProductoTallaEntity> findByProductoIdAndStockGreaterThan(Long productoId, int minStock);
}
