package com.camisastienda.domain.model;

import com.camisastienda.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Modelo de dominio de una camiseta de fútbol con su lista de tallas y stock. */
public class Producto {
    private Long id;
    private String nombre;
    private String descripcion;
    private String equipo;
    private String liga;
    private String temporada;
    private BigDecimal precio;
    private String imagenUrl;
    private boolean activo;
    private List<ProductoTalla> tallas;
    private Instant creadoEn;

    public Producto() {
        this.tallas = new ArrayList<>();
    }

    public Producto(Long id, String nombre, String descripcion, String equipo, String liga,
                    String temporada, BigDecimal precio, String imagenUrl, Boolean activo,
                    List<ProductoTalla> tallas, Instant creadoEn) {
        validarCampos(nombre, equipo, liga, temporada, precio);
        this.id = id;
        this.nombre = nombre.trim();
        this.descripcion = descripcion != null ? descripcion.trim() : null;
        this.equipo = equipo.trim();
        this.liga = liga.trim();
        this.temporada = temporada.trim();
        this.precio = precio;
        this.imagenUrl = imagenUrl;
        this.activo = activo != null ? activo : true;
        this.tallas = tallas != null ? new ArrayList<>(tallas) : new ArrayList<>();
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
    }

    public static Producto nuevo(String nombre, String descripcion, String equipo, String liga,
                                 String temporada, BigDecimal precio, String imagenUrl) {
        return new Producto(null, nombre, descripcion, equipo, liga, temporada, precio, imagenUrl, true, new ArrayList<>(), Instant.now());
    }

    // Valida datos obligatorios y que el precio no sea negativo
    private void validarCampos(String nombre, String equipo, String liga, String temporada, BigDecimal precio) {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("El nombre del producto no puede estar vacío");
        }
        if (equipo == null || equipo.isBlank()) {
            throw new ReglaNegocioException("El equipo del producto es obligatorio");
        }
        if (liga == null || liga.isBlank()) {
            throw new ReglaNegocioException("La liga del producto es obligatoria");
        }
        if (temporada == null || temporada.isBlank()) {
            throw new ReglaNegocioException("La temporada del producto es obligatoria");
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException("El precio del producto debe ser mayor o igual a cero");
        }
    }

    // Agrega una talla al producto verificando que no esté repetida
    public void agregarTalla(ProductoTalla nuevaTalla) {
        if (nuevaTalla == null) {
            throw new ReglaNegocioException("La talla a agregar no puede ser nula");
        }
        boolean yaExiste = this.tallas.stream().anyMatch(t -> t.getTalla() == nuevaTalla.getTalla());
        if (yaExiste) {
            throw new ReglaNegocioException("La talla " + nuevaTalla.getTalla() + " ya está registrada en este producto");
        }
        nuevaTalla.setProductoId(this.id);
        this.tallas.add(nuevaTalla);
    }

    // Busca una talla específica dentro del producto
    public Optional<ProductoTalla> buscarTalla(Talla talla) {
        if (talla == null) return Optional.empty();
        return this.tallas.stream()
                .filter(t -> t.getTalla() == talla)
                .findFirst();
    }

    // Indica si el producto está activo y tiene al menos una talla con stock > 0
    public boolean tieneStockDisponible() {
        return this.activo && this.tallas.stream().anyMatch(t -> t.getStock() > 0);
    }

    public void desactivar() {
        this.activo = false;
    }

    public void activar() {
        this.activo = true;
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

    public List<ProductoTalla> getTallas() {
        return Collections.unmodifiableList(tallas);
    }

    public void setTallas(List<ProductoTalla> tallas) {
        this.tallas = tallas != null ? new ArrayList<>(tallas) : new ArrayList<>();
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Producto producto)) return false;
        return Objects.equals(id, producto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
