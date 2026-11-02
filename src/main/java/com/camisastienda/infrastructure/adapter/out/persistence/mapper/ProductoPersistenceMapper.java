package com.camisastienda.infrastructure.adapter.out.persistence.mapper;

import com.camisastienda.domain.model.Producto;
import com.camisastienda.domain.model.ProductoTalla;
import com.camisastienda.domain.model.Talla;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.ProductoEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.ProductoTallaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/** Convierte entre Producto/ProductoTalla (dominio) y sus entidades JPA. */
@Component
public class ProductoPersistenceMapper {

    public Producto toDomain(ProductoEntity entity) {
        if (entity == null) return null;
        List<ProductoTalla> tallas = entity.getTallas() == null ? List.of() :
                entity.getTallas().stream().map(this::tallaToDomain).toList();
        return new Producto(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getEquipo(),
                entity.getLiga(),
                entity.getTemporada(),
                entity.getPrecio(),
                entity.getImagenUrl(),
                entity.isActivo(),
                tallas,
                entity.getCreadoEn()
        );
    }

    public ProductoTalla tallaToDomain(ProductoTallaEntity entity) {
        if (entity == null) return null;
        Long productoId = entity.getProducto() != null ? entity.getProducto().getId() : null;
        return new ProductoTalla(
                entity.getId(),
                productoId,
                Talla.desdeTexto(entity.getTalla()),
                entity.getStock(),
                entity.getVersion()
        );
    }

    public ProductoEntity toEntity(Producto domain) {
        if (domain == null) return null;
        ProductoEntity entity = new ProductoEntity(
                domain.getId(),
                domain.getNombre(),
                domain.getDescripcion(),
                domain.getEquipo(),
                domain.getLiga(),
                domain.getTemporada(),
                domain.getPrecio(),
                domain.getImagenUrl(),
                domain.isActivo(),
                domain.getCreadoEn()
        );
        if (domain.getTallas() != null) {
            for (ProductoTalla t : domain.getTallas()) {
                entity.agregarTalla(tallaToEntity(t, entity));
            }
        }
        return entity;
    }

    public ProductoTallaEntity tallaToEntity(ProductoTalla domain, ProductoEntity parent) {
        if (domain == null) return null;
        return new ProductoTallaEntity(
                domain.getId(),
                parent,
                domain.getTalla().name(),
                domain.getStock(),
                domain.getVersion()
        );
    }
}
