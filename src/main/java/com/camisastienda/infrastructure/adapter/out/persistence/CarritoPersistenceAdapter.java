package com.camisastienda.infrastructure.adapter.out.persistence;

import com.camisastienda.application.port.out.CarritoRepositoryPort;
import com.camisastienda.domain.exception.RecursoNoEncontradoException;
import com.camisastienda.domain.model.Carrito;
import com.camisastienda.domain.model.CarritoItem;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.CarritoEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.CarritoItemEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.ProductoTallaEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.mapper.CarritoPersistenceMapper;
import com.camisastienda.infrastructure.adapter.out.persistence.repository.CarritoJpaRepository;
import com.camisastienda.infrastructure.adapter.out.persistence.repository.ProductoTallaJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/** Adaptador de persistencia que guarda y consulta el carrito del cliente en MySQL. */
@Component
public class CarritoPersistenceAdapter implements CarritoRepositoryPort {

    private final CarritoJpaRepository carritoRepo;
    private final ProductoTallaJpaRepository tallaRepo;
    private final CarritoPersistenceMapper mapper;

    public CarritoPersistenceAdapter(CarritoJpaRepository carritoRepo,
                                     ProductoTallaJpaRepository tallaRepo,
                                     CarritoPersistenceMapper mapper) {
        this.carritoRepo = carritoRepo;
        this.tallaRepo = tallaRepo;
        this.mapper = mapper;
    }

    @Override
    public Optional<Carrito> buscarPorUsuarioId(Long usuarioId) {
        return carritoRepo.findByUsuarioId(usuarioId).map(mapper::toDomain);
    }

    // Guarda el carrito sincronizando su lista de ítems con la BD
    @Override
    @Transactional
    public Carrito guardar(Carrito carrito) {
        CarritoEntity entity = carrito.getId() != null
                ? carritoRepo.findById(carrito.getId()).orElse(new CarritoEntity(carrito.getId(), carrito.getUsuarioId(), carrito.getActualizadoEn()))
                : carritoRepo.findByUsuarioId(carrito.getUsuarioId()).orElse(new CarritoEntity(null, carrito.getUsuarioId(), carrito.getActualizadoEn()));

        entity.setUsuarioId(carrito.getUsuarioId());
        entity.setActualizadoEn(carrito.getActualizadoEn());
        entity.getItems().clear();

        for (CarritoItem item : carrito.getItems()) {
            ProductoTallaEntity pt = tallaRepo.findById(item.getProductoTallaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Talla no encontrada: " + item.getProductoTallaId()));
            CarritoItemEntity itemEntity = new CarritoItemEntity(item.getId(), entity, pt, item.getCantidad());
            entity.agregarItem(itemEntity);
        }

        CarritoEntity saved = carritoRepo.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void eliminarPorUsuarioId(Long usuarioId) {
        carritoRepo.deleteByUsuarioId(usuarioId);
    }
}
