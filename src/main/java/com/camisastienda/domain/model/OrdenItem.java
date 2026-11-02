package com.camisastienda.domain.model;

import com.camisastienda.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.util.Objects;

public class OrdenItem {
    private Long id;
    private Long ordenId;
    private Long productoTallaId;
    private String nombreProducto;
    private Talla talla;
    private int cantidad;
    private BigDecimal precioUnitario;

    public OrdenItem() {
    }

    public OrdenItem(Long id, Long ordenId, Long productoTallaId, String nombreProducto,
                     Talla talla, int cantidad, BigDecimal precioUnitario) {
        if (productoTallaId == null) {
            throw new ReglaNegocioException("El ID de la talla es obligatorio para el ítem de la orden");
        }
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor a cero");
        }
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException("El precio unitario no puede ser negativo");
        }
        this.id = id;
        this.ordenId = ordenId;
        this.productoTallaId = productoTallaId;
        this.nombreProducto = nombreProducto;
        this.talla = talla;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public static OrdenItem desdeCarritoItem(CarritoItem item) {
        if (item == null) {
            throw new ReglaNegocioException("El ítem de carrito no puede ser nulo");
        }
        return new OrdenItem(
                null,
                null,
                item.getProductoTallaId(),
                item.getNombreProducto(),
                item.getTalla(),
                item.getCantidad(),
                item.getPrecioUnitario()
        );
    }

    public BigDecimal calcularSubtotal() {
        return this.precioUnitario.multiply(BigDecimal.valueOf(this.cantidad));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrdenId() {
        return ordenId;
    }

    public void setOrdenId(Long ordenId) {
        this.ordenId = ordenId;
    }

    public Long getProductoTallaId() {
        return productoTallaId;
    }

    public void setProductoTallaId(Long productoTallaId) {
        this.productoTallaId = productoTallaId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Talla getTalla() {
        return talla;
    }

    public void setTalla(Talla talla) {
        this.talla = talla;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrdenItem ordenItem)) return false;
        return Objects.equals(id, ordenItem.id) ||
                (Objects.equals(ordenId, ordenItem.ordenId) && Objects.equals(productoTallaId, ordenItem.productoTallaId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ordenId, productoTallaId);
    }
}
