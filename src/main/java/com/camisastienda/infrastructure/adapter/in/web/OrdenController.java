package com.camisastienda.infrastructure.adapter.in.web;

import com.camisastienda.application.port.in.CheckoutPort;
import com.camisastienda.application.port.in.ListarMisOrdenesPort;
import com.camisastienda.application.port.in.VerDetalleOrdenPort;
import com.camisastienda.domain.model.Orden;
import com.camisastienda.domain.model.OrdenItem;
import com.camisastienda.infrastructure.security.UsuarioPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Controlador REST para el checkout y la consulta de órdenes (solo CLIENTE).
 */
@RestController
@RequestMapping("/api/ordenes")
@Tag(name = "Órdenes", description = "Checkout y consulta de órdenes de compra del cliente")
public class OrdenController {

    private final CheckoutPort checkout;
    private final ListarMisOrdenesPort listarMisOrdenes;
    private final VerDetalleOrdenPort verDetalleOrden;

    public OrdenController(CheckoutPort checkout,
                           ListarMisOrdenesPort listarMisOrdenes,
                           VerDetalleOrdenPort verDetalleOrden) {
        this.checkout = checkout;
        this.listarMisOrdenes = listarMisOrdenes;
        this.verDetalleOrden = verDetalleOrden;
    }

    // ── POST /api/ordenes/checkout ──────────────────────────────────────────────

    @Operation(summary = "Realizar checkout",
            description = "Crea una orden desde el carrito, descuenta stock y vacía el carrito en una transacción atómica.")
    @PostMapping("/checkout")
    public ResponseEntity<OrdenResponse> checkout(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        Orden orden = checkout.ejecutar(usuario.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(OrdenResponse.desdeDomain(orden));
    }

    // ── GET /api/ordenes ────────────────────────────────────────────────────────

    @Operation(summary = "Mis órdenes",
            description = "Devuelve el historial de órdenes del cliente autenticado ordenadas de forma descendente por fecha.")
    @GetMapping
    public ResponseEntity<List<OrdenResponse>> listarMisOrdenes(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        List<Orden> ordenes = listarMisOrdenes.ejecutar(usuario.id());
        List<OrdenResponse> response = ordenes.stream()
                .map(OrdenResponse::desdeDomain)
                .toList();
        return ResponseEntity.ok(response);
    }

    // ── GET /api/ordenes/{id} ───────────────────────────────────────────────────

    @Operation(summary = "Detalle de orden",
            description = "Devuelve el detalle de una orden perteneciente al cliente autenticado.")
    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponse> verDetalle(@AuthenticationPrincipal UsuarioPrincipal usuario,
                                                    @PathVariable Long id) {
        Orden orden = verDetalleOrden.ejecutar(usuario.id(), id);
        return ResponseEntity.ok(OrdenResponse.desdeDomain(orden));
    }

    // ── DTOs (Records) ──────────────────────────────────────────────────────────

    /** Detalle de un ítem dentro de la orden. */
    public record OrdenItemResponse(
            Long id,
            Long productoTallaId,
            String nombreProducto,
            String talla,
            int cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal) {

        public static OrdenItemResponse desdeDomain(OrdenItem item) {
            return new OrdenItemResponse(
                    item.getId(),
                    item.getProductoTallaId(),
                    item.getNombreProducto(),
                    item.getTalla() != null ? item.getTalla().name() : null,
                    item.getCantidad(),
                    item.getPrecioUnitario(),
                    item.calcularSubtotal());
        }
    }

    /** Representación completa de la orden de compra. */
    public record OrdenResponse(
            Long id,
            Long usuarioId,
            BigDecimal total,
            String estado,
            Instant creadoEn,
            List<OrdenItemResponse> items) {

        public static OrdenResponse desdeDomain(Orden orden) {
            List<OrdenItemResponse> items = orden.getItems() != null
                    ? orden.getItems().stream().map(OrdenItemResponse::desdeDomain).toList()
                    : List.of();
            return new OrdenResponse(
                    orden.getId(),
                    orden.getUsuarioId(),
                    orden.getTotal(),
                    orden.getEstado() != null ? orden.getEstado().name() : null,
                    orden.getCreadoEn(),
                    items);
        }
    }
}

