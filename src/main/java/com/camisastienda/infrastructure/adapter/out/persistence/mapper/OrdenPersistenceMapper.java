package com.camisastienda.infrastructure.adapter.out.persistence.mapper;

import com.camisastienda.domain.model.EstadoOrden;
import com.camisastienda.domain.model.Orden;
import com.camisastienda.domain.model.OrdenItem;
import com.camisastienda.domain.model.Talla;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.OrdenEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.OrdenItemEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.ProductoEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.ProductoTallaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/** Convierte OrdenEntity y sus ítems al modelo de dominio Orden. */
@Component
public class OrdenPersistenceMapper {

    public Orden toDomain(OrdenEntity entity) {
        if (entity == null) return null;
        List<OrdenItem> items = entity.getItems() == null ? List.of() :
                entity.getItems().stream().map(this::itemToDomain).toList();
        return new Orden(
                entity.getId(),
                entity.getUsuarioId(),
                items,
                entity.getTotal(),
                EstadoOrden.desdeTexto(entity.getEstado()),
                entity.getCreadoEn()
        );
    }

    public OrdenItem itemToDomain(OrdenItemEntity entity) {
        if (entity == null) return null;
        ProductoTallaEntity pt = entity.getProductoTalla();
        ProductoEntity prod = pt != null ? pt.getProducto() : null;

        Long ptId = pt != null ? pt.getId() : null;
        String nombreProd = prod != null ? prod.getNombre() : "Camiseta";
        Talla talla = pt != null && pt.getTalla() != null ? Talla.desdeTexto(pt.getTalla()) : Talla.M;
        Long ordenId = entity.getOrden() != null ? entity.getOrden().getId() : null;

        return new OrdenItem(
                entity.getId(),
                ordenId,
                ptId,
                nombreProd,
                talla,
                entity.getCantidad(),
                entity.getPrecioUnitario()
        );
    }
}
