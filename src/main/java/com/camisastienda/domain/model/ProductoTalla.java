package com.camisastienda.domain.model;

import com.camisastienda.domain.exception.ReglaNegocioException;
import com.camisastienda.domain.exception.StockInsuficienteException;

import java.util.Objects;

/** Representa el stock disponible para una talla específica de una camiseta. */
public class ProductoTalla {
    private Long id;
    private Long productoId;
    private Talla talla;
    private int stock;
    private Long version; // Para bloqueo optimista (evita sobreventa concurrente)

    public ProductoTalla() {
    }

    public ProductoTalla(Long id, Long productoId, Talla talla, int stock, Long version) {
        if (talla == null) {
            throw new ReglaNegocioException("La talla es obligatoria");
        }
        if (stock < 0) {
            throw new ReglaNegocioException("El stock no puede ser negativo");
        }
        this.id = id;
        this.productoId = productoId;
        this.talla = talla;
        this.stock = stock;
        this.version = version != null ? version : 0L;
    }

    public static ProductoTalla nueva(Long productoId, Talla talla, int stockInicial) {
        return new ProductoTalla(null, productoId, talla, stockInicial, 0L);
    }

    // Resta unidades del stock al comprar; lanza error si no alcanza
    public void descontarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad a descontar debe ser mayor a cero");
        }
        if (this.stock < cantidad) {
            throw new StockInsuficienteException("Camiseta (ID: " + productoId + ")", this.talla.name(), cantidad, this.stock);
        }
        this.stock -= cantidad;
    }

    // Suma unidades al inventario
    public void aumentarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad a aumentar debe ser mayor a cero");
        }
        this.stock += cantidad;
    }

    // Indica si hay suficientes unidades para cubrir la cantidad pedida
    public boolean hayStock(int cantidad) {
        return cantidad > 0 && this.stock >= cantidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Talla getTalla() {
        return talla;
    }

    public void setTalla(Talla talla) {
        this.talla = talla;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new ReglaNegocioException("El stock no puede ser negativo");
        }
        this.stock = stock;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductoTalla that)) return false;
        return Objects.equals(id, that.id) ||
                (Objects.equals(productoId, that.productoId) && talla == that.talla);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productoId, talla);
    }
}
