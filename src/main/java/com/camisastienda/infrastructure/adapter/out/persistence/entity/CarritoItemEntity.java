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

@Entity
@Table(
        name = "carrito_items",
        uniqueConstraints = @UniqueConstraint(name = "uq_carrito_item", columnNames = {"carrito_id", "producto_talla_id"})
)
public class CarritoItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    private CarritoEntity carrito;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_talla_id", nullable = false)
    private ProductoTallaEntity productoTalla;

    @Column(nullable = false)
    private int cantidad;

    public CarritoItemEntity() {
    }

    public CarritoItemEntity(Long id, CarritoEntity carrito, ProductoTallaEntity productoTalla, int cantidad) {
        this.id = id;
        this.carrito = carrito;
        this.productoTalla = productoTalla;
        this.cantidad = cantidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CarritoEntity getCarrito() {
        return carrito;
    }

    public void setCarrito(CarritoEntity carrito) {
        this.carrito = carrito;
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
}
