package com.camisastienda.infrastructure.adapter.out.persistence;

import com.camisastienda.application.port.out.ProductoRepositoryPort;
import com.camisastienda.domain.exception.ProductoNoEncontradoException;
import com.camisastienda.domain.model.Producto;
import com.camisastienda.domain.model.ProductoTalla;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.ProductoEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.ProductoTallaEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.mapper.ProductoPersistenceMapper;
import com.camisastienda.infrastructure.adapter.out.persistence.repository.ProductoJpaRepository;
import com.camisastienda.infrastructure.adapter.out.persistence.repository.ProductoTallaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductoPersistenceAdapter implements ProductoRepositoryPort {

    private final ProductoJpaRepository productoRepo;
    private final ProductoTallaJpaRepository tallaRepo;
    private final ProductoPersistenceMapper mapper;

    public ProductoPersistenceAdapter(ProductoJpaRepository productoRepo,
                                      ProductoTallaJpaRepository tallaRepo,
                                      ProductoPersistenceMapper mapper) {
        this.productoRepo = productoRepo;
        this.tallaRepo = tallaRepo;
        this.mapper = mapper;
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        return productoRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Producto> listarTodos(boolean soloActivos) {
        List<ProductoEntity> entities = soloActivos ? productoRepo.findByActivoTrue() : productoRepo.findAll();
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Producto> buscarConFiltros(String equipo, String liga, String nombre, Boolean soloActivos) {
        return productoRepo.buscarConFiltros(equipo, liga, nombre, soloActivos)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Producto guardar(Producto producto) {
        ProductoEntity entity = mapper.toEntity(producto);
        ProductoEntity saved = productoRepo.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void eliminarPorId(Long id) {
        productoRepo.deleteById(id);
    }

    @Override
    public Optional<ProductoTalla> buscarTallaPorId(Long productoTallaId) {
        return tallaRepo.findById(productoTallaId).map(mapper::tallaToDomain);
    }

    @Override
    public Optional<ProductoTalla> buscarTallaPorProductoIdYTalla(Long productoId, String talla) {
        return tallaRepo.findByProductoIdAndTallaIgnoreCase(productoId, talla).map(mapper::tallaToDomain);
    }

    @Override
    public ProductoTalla guardarTalla(ProductoTalla talla) {
        ProductoEntity prod = productoRepo.findById(talla.getProductoId())
                .orElseThrow(() -> new ProductoNoEncontradoException(talla.getProductoId()));
        ProductoTallaEntity entity = mapper.tallaToEntity(talla, prod);
        ProductoTallaEntity saved = tallaRepo.saveAndFlush(entity);
        return mapper.tallaToDomain(saved);
    }
}
