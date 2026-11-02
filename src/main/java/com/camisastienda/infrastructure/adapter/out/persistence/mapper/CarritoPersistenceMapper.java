package com.camisastienda.infrastructure.adapter.out.persistence.mapper;

import com.camisastienda.domain.model.Carrito;
import com.camisastienda.domain.model.CarritoItem;
import com.camisastienda.domain.model.Talla;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.CarritoEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.CarritoItemEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.ProductoEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.ProductoTallaEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/** Convierte CarritoEntity y sus ítems al modelo de dominio Carrito. */
@Component
public class CarritoPersistenceMapper {

    public Carrito toDomain(CarritoEntity entity) {
        if (entity == null) return null;
        List<CarritoItem> items = entity.getItems() == null ? List.of() :
                entity.getItems().stream().map(this::itemToDomain).toList();
        return new Carrito(
                entity.getId(),
                entity.getUsuarioId(),
                items,
                entity.getActualizadoEn()
        );
    }

    public CarritoItem itemToDomain(CarritoItemEntity entity) {
        if (entity == null) return null;
        ProductoTallaEntity pt = entity.getProductoTalla();
        ProductoEntity prod = pt != null ? pt.getProducto() : null;

        Long ptId = pt != null ? pt.getId() : null;
        String nombreProd = prod != null ? prod.getNombre() : "Camiseta";
        Talla talla = pt != null && pt.getTalla() != null ? Talla.desdeTexto(pt.getTalla()) : Talla.M;
        BigDecimal precio = prod != null && prod.getPrecio() != null ? prod.getPrecio() : BigDecimal.ZERO;
        Long carritoId = entity.getCarrito() != null ? entity.getCarrito().getId() : null;

        return new CarritoItem(
                entity.getId(),
                carritoId,
                ptId,
                nombreProd,
                talla,
                precio,
                entity.getCantidad()
        );
    }
}
