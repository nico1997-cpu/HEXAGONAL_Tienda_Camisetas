package com.camisastienda.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

/** Entidad JPA que mapea la tabla 'producto_tallas' (stock por talla). */
@Entity
@Table(
        name = "producto_tallas",
        uniqueConstraints = @UniqueConstraint(name = "uq_producto_talla", columnNames = {"producto_id", "talla"})
)
public class ProductoTallaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;

    @Column(nullable = false, length = 10)
    private String talla;

    @Column(nullable = false)
    private int stock;

    // @Version activa el bloqueo optimista de Hibernate para evitar ventas simultáneas sin stock
    @Version
    @Column(nullable = false)
    private Long version = 0L;

    public ProductoTallaEntity() {
    }

    public ProductoTallaEntity(Long id, ProductoEntity producto, String talla, int stock, Long version) {
        this.id = id;
        this.producto = producto;
        this.talla = talla;
        this.stock = stock;
        this.version = version != null ? version : 0L;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductoEntity getProducto() {
        return producto;
    }

    public void setProducto(ProductoEntity producto) {
        this.producto = producto;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
