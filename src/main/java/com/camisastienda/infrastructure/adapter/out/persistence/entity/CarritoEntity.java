package com.camisastienda.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Entidad JPA que mapea la tabla 'carritos'. */
@Entity
@Table(name = "carritos")
public class CarritoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false, unique = true)
    private Long usuarioId;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CarritoItemEntity> items = new ArrayList<>();

    public CarritoEntity() {
    }

    public CarritoEntity(Long id, Long usuarioId, Instant actualizadoEn) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.actualizadoEn = actualizadoEn != null ? actualizadoEn : Instant.now();
    }

    public void agregarItem(CarritoItemEntity item) {
        item.setCarrito(this);
        this.items.add(item);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Instant getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(Instant actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    public List<CarritoItemEntity> getItems() {
        return items;
    }

    public void setItems(List<CarritoItemEntity> items) {
        this.items.clear();
        if (items != null) {
            items.forEach(this::agregarItem);
        }
    }
}
