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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Entidad JPA que mapea la tabla 'ordenes'. */
@Entity
@Table(name = "ordenes")
public class OrdenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrdenItemEntity> items = new ArrayList<>();

    public OrdenEntity() {
    }

    public OrdenEntity(Long id, Long usuarioId, BigDecimal total, String estado, Instant creadoEn) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.total = total;
        this.estado = estado;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
    }

    public void agregarItem(OrdenItemEntity item) {
        item.setOrden(this);
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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }

    public List<OrdenItemEntity> getItems() {
        return items;
    }

    public void setItems(List<OrdenItemEntity> items) {
        this.items.clear();
        if (items != null) {
            items.forEach(this::agregarItem);
        }
    }
}
