package com.camisastienda.domain.model;

import com.camisastienda.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.util.Objects;

public class CarritoItem {
    private Long id;
    private Long carritoId;
    private Long productoTallaId;
    private String nombreProducto;
    private Talla talla;
    private BigDecimal precioUnitario;
    private int cantidad;

    public CarritoItem() {
    }

    public CarritoItem(Long id, Long carritoId, Long productoTallaId, String nombreProducto,
                       Talla talla, BigDecimal precioUnitario, int cantidad) {
        if (productoTallaId == null) {
            throw new ReglaNegocioException("El ID de la talla del producto es obligatorio");
        }
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor a cero");
        }
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException("El precio unitario no puede ser negativo");
        }
        this.id = id;
        this.carritoId = carritoId;
        this.productoTallaId = productoTallaId;
        this.nombreProducto = nombreProducto;
        this.talla = talla;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }

    public static CarritoItem crear(Long productoTallaId, String nombreProducto, Talla talla,
                                    BigDecimal precioUnitario, int cantidad) {
        return new CarritoItem(null, null, productoTallaId, nombreProducto, talla, precioUnitario, cantidad);
    }

    public void actualizarCantidad(int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor a cero");
        }
        this.cantidad = nuevaCantidad;
    }

    public void sumarCantidad(int adicional) {
        if (adicional <= 0) {
            throw new ReglaNegocioException("La cantidad a sumar debe ser mayor a cero");
        }
        this.cantidad += adicional;
    }

    public BigDecimal calcularSubtotal() {
        if (this.precioUnitario == null) return BigDecimal.ZERO;
        return this.precioUnitario.multiply(BigDecimal.valueOf(this.cantidad));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCarritoId() {
        return carritoId;
    }

    public void setCarritoId(Long carritoId) {
        this.carritoId = carritoId;
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

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CarritoItem that)) return false;
        return Objects.equals(id, that.id) ||
                (Objects.equals(carritoId, that.carritoId) && Objects.equals(productoTallaId, that.productoTallaId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, carritoId, productoTallaId);
    }
}
