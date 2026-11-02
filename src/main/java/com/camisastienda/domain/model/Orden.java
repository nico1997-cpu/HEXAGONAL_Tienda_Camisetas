package com.camisastienda.domain.model;

import com.camisastienda.domain.exception.CarritoVacioException;
import com.camisastienda.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Orden {
    private Long id;
    private Long usuarioId;
    private List<OrdenItem> items;
    private BigDecimal total;
    private EstadoOrden estado;
    private Instant creadoEn;

    public Orden() {
        this.items = new ArrayList<>();
    }

    public Orden(Long id, Long usuarioId, List<OrdenItem> items, BigDecimal total,
                 EstadoOrden estado, Instant creadoEn) {
        if (usuarioId == null) {
            throw new ReglaNegocioException("El ID del usuario es obligatorio para la orden");
        }
        if (items == null || items.isEmpty()) {
            throw new CarritoVacioException("No se puede crear una orden sin ítems");
        }
        if (total == null || total.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException("El total de la orden debe ser mayor o igual a cero");
        }
        this.id = id;
        this.usuarioId = usuarioId;
        this.items = new ArrayList<>(items);
        this.total = total;
        this.estado = estado != null ? estado : EstadoOrden.CREADA;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
    }

    public static Orden crearDesdeCarrito(Carrito carrito, Long usuarioId) {
        if (carrito == null || carrito.estaVacio()) {
            throw new CarritoVacioException("No se puede generar una orden a partir de un carrito vacío");
        }
        List<OrdenItem> ordenItems = carrito.getItems().stream()
                .map(OrdenItem::desdeCarritoItem)
                .toList();

        BigDecimal totalCalculado = ordenItems.stream()
                .map(OrdenItem::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Orden(null, usuarioId, ordenItems, totalCalculado, EstadoOrden.CREADA, Instant.now());
    }

    public void cancelar() {
        if (EstadoOrden.COMPLETADA.equals(this.estado)) {
            throw new ReglaNegocioException("No se puede cancelar una orden que ya fue completada");
        }
        if (EstadoOrden.CANCELADA.equals(this.estado)) {
            throw new ReglaNegocioException("La orden ya se encuentra cancelada");
        }
        this.estado = EstadoOrden.CANCELADA;
    }

    public void pagar() {
        if (!EstadoOrden.CREADA.equals(this.estado)) {
            throw new ReglaNegocioException("Solo se pueden pagar órdenes en estado CREADA");
        }
        this.estado = EstadoOrden.PAGADA;
    }

    public void completar() {
        if (!EstadoOrden.PAGADA.equals(this.estado) && !EstadoOrden.CREADA.equals(this.estado)) {
            throw new ReglaNegocioException("Solo órdenes pagadas o creadas pueden ser completadas");
        }
        this.estado = EstadoOrden.COMPLETADA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        if (this.items != null) {
            this.items.forEach(item -> item.setOrdenId(id));
        }
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<OrdenItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<OrdenItem> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
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
        if (!(o instanceof Orden orden)) return false;
        return Objects.equals(id, orden.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
