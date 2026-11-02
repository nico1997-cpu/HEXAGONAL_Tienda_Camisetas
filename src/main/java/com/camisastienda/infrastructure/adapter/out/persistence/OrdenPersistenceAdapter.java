package com.camisastienda.infrastructure.adapter.out.persistence;

import com.camisastienda.application.port.out.OrdenRepositoryPort;
import com.camisastienda.domain.exception.RecursoNoEncontradoException;
import com.camisastienda.domain.model.Orden;
import com.camisastienda.domain.model.OrdenItem;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.OrdenEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.OrdenItemEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.ProductoTallaEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.mapper.OrdenPersistenceMapper;
import com.camisastienda.infrastructure.adapter.out.persistence.repository.OrdenJpaRepository;
import com.camisastienda.infrastructure.adapter.out.persistence.repository.ProductoTallaJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class OrdenPersistenceAdapter implements OrdenRepositoryPort {

    private final OrdenJpaRepository ordenRepo;
    private final ProductoTallaJpaRepository tallaRepo;
    private final OrdenPersistenceMapper mapper;

    public OrdenPersistenceAdapter(OrdenJpaRepository ordenRepo,
                                   ProductoTallaJpaRepository tallaRepo,
                                   OrdenPersistenceMapper mapper) {
        this.ordenRepo = ordenRepo;
        this.tallaRepo = tallaRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Orden guardar(Orden orden) {
        OrdenEntity entity = new OrdenEntity(
                orden.getId(),
                orden.getUsuarioId(),
                orden.getTotal(),
                orden.getEstado().name(),
                orden.getCreadoEn()
        );

        for (OrdenItem item : orden.getItems()) {
            ProductoTallaEntity pt = tallaRepo.findById(item.getProductoTallaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Talla no encontrada: " + item.getProductoTallaId()));
            OrdenItemEntity itemEntity = new OrdenItemEntity(
                    item.getId(),
                    entity,
                    pt,
                    item.getCantidad(),
                    item.getPrecioUnitario()
            );
            entity.agregarItem(itemEntity);
        }

        OrdenEntity saved = ordenRepo.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Orden> buscarPorId(Long id) {
        return ordenRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Orden> buscarPorUsuarioId(Long usuarioId) {
        return ordenRepo.findByUsuarioIdOrderByCreadoEnDesc(usuarioId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
