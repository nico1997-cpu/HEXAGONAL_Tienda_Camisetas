package com.camisastienda.domain.model;

import com.camisastienda.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Carrito {
    private Long id;
    private Long usuarioId;
    private List<CarritoItem> items;
    private Instant actualizadoEn;

    public Carrito() {
        this.items = new ArrayList<>();
    }

    public Carrito(Long id, Long usuarioId, List<CarritoItem> items, Instant actualizadoEn) {
        if (usuarioId == null) {
            throw new ReglaNegocioException("El ID del usuario es obligatorio para el carrito");
        }
        this.id = id;
        this.usuarioId = usuarioId;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.actualizadoEn = actualizadoEn != null ? actualizadoEn : Instant.now();
    }

    public static Carrito paraUsuario(Long usuarioId) {
        return new Carrito(null, usuarioId, new ArrayList<>(), Instant.now());
    }

    public void agregarItem(CarritoItem nuevoItem) {
        if (nuevoItem == null) {
            throw new ReglaNegocioException("El ítem a agregar no puede ser nulo");
        }
        Optional<CarritoItem> existente = buscarItemPorProductoTalla(nuevoItem.getProductoTallaId());
        if (existente.isPresent()) {
            existente.get().sumarCantidad(nuevoItem.getCantidad());
        } else {
            nuevoItem.setCarritoId(this.id);
            this.items.add(nuevoItem);
        }
        this.actualizadoEn = Instant.now();
    }

    public void actualizarCantidad(Long productoTallaId, int nuevaCantidad) {
        CarritoItem item = buscarItemPorProductoTalla(productoTallaId)
                .orElseThrow(() -> new ReglaNegocioException("El producto no se encuentra en el carrito"));
        item.actualizarCantidad(nuevaCantidad);
        this.actualizadoEn = Instant.now();
    }

    public void quitarItem(Long productoTallaId) {
        boolean removido = this.items.removeIf(item -> Objects.equals(item.getProductoTallaId(), productoTallaId));
        if (!removido) {
            throw new ReglaNegocioException("El ítem a eliminar no existe en el carrito");
        }
        this.actualizadoEn = Instant.now();
    }

    public void vaciar() {
        this.items.clear();
        this.actualizadoEn = Instant.now();
    }

    public BigDecimal calcularTotal() {
        return this.items.stream()
                .map(CarritoItem::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean estaVacio() {
        return this.items.isEmpty();
    }

    public int contarTotalUnidades() {
        return this.items.stream().mapToInt(CarritoItem::getCantidad).sum();
    }

    public Optional<CarritoItem> buscarItemPorProductoTalla(Long productoTallaId) {
        if (productoTallaId == null) return Optional.empty();
        return this.items.stream()
                .filter(i -> Objects.equals(i.getProductoTallaId(), productoTallaId))
                .findFirst();
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

    public List<CarritoItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<CarritoItem> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    public Instant getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(Instant actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Carrito carrito)) return false;
        return Objects.equals(id, carrito.id) || Objects.equals(usuarioId, carrito.usuarioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, usuarioId);
    }
}
