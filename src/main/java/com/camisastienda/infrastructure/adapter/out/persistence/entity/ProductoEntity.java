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

/** Entidad JPA que mapea la tabla 'productos' (camisetas) y su relación con tallas. */
@Entity
@Table(name = "productos")
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, length = 100)
    private String equipo;

    @Column(nullable = false, length = 100)
    private String liga;

    @Column(nullable = false, length = 20)
    private String temporada;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    // Relación 1 a muchos con las tallas del producto
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ProductoTallaEntity> tallas = new ArrayList<>();

    public ProductoEntity() {
    }

    public ProductoEntity(Long id, String nombre, String descripcion, String equipo, String liga,
                          String temporada, BigDecimal precio, String imagenUrl, boolean activo, Instant creadoEn) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.equipo = equipo;
        this.liga = liga;
        this.temporada = temporada;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
        this.activo = activo;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
    }

    public void agregarTalla(ProductoTallaEntity tallaEntity) {
        tallaEntity.setProducto(this);
        this.tallas.add(tallaEntity);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    public String getLiga() {
        return liga;
    }

    public void setLiga(String liga) {
        this.liga = liga;
    }

    public String getTemporada() {
        return temporada;
    }

    public void setTemporada(String temporada) {
        this.temporada = temporada;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }

    public List<ProductoTallaEntity> getTallas() {
        return tallas;
    }

    public void setTallas(List<ProductoTallaEntity> tallas) {
        this.tallas.clear();
        if (tallas != null) {
            tallas.forEach(this::agregarTalla);
        }
    }
}
