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

import java.math.BigDecimal;

/** Entidad JPA que mapea la tabla 'orden_items' (detalle comprado en una orden). */
@Entity
@Table(name = "orden_items")
public class OrdenItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private OrdenEntity orden;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_talla_id", nullable = false)
    private ProductoTallaEntity productoTalla;

    @Column(nullable = false)
    private int cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    public OrdenItemEntity() {
    }

    public OrdenItemEntity(Long id, OrdenEntity orden, ProductoTallaEntity productoTalla, int cantidad, BigDecimal precioUnitario) {
        this.id = id;
        this.orden = orden;
        this.productoTalla = productoTalla;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrdenEntity getOrden() {
        return orden;
    }

    public void setOrden(OrdenEntity orden) {
        this.orden = orden;
    }

    public ProductoTallaEntity getProductoTalla() {
        return productoTalla;
    }

    public void setProductoTalla(ProductoTallaEntity productoTalla) {
        this.productoTalla = productoTalla;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
