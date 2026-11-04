package com.camisastienda.infrastructure.adapter.in.web;

import com.camisastienda.application.port.in.ActualizarItemCarritoPort;
import com.camisastienda.application.port.in.AgregarItemCarritoPort;
import com.camisastienda.application.port.in.QuitarItemCarritoPort;
import com.camisastienda.application.port.in.VaciarCarritoPort;
import com.camisastienda.application.port.in.VerCarritoPort;
import com.camisastienda.domain.model.Carrito;
import com.camisastienda.domain.model.CarritoItem;
import com.camisastienda.infrastructure.security.UsuarioPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Controlador REST del carrito de compras (solo CLIENTE, regla en SecurityConfig).
 * El usuarioId sale del claim "usuarioId" del token JWT (principal UsuarioPrincipal).
 */
@RestController
@RequestMapping("/api/carrito")
@Tag(name = "Carrito", description = "Carrito de compras del cliente autenticado")
public class CarritoController {

    private final VerCarritoPort verCarrito;
    private final AgregarItemCarritoPort agregarItem;
    private final ActualizarItemCarritoPort actualizarItem;
    private final QuitarItemCarritoPort quitarItem;
    private final VaciarCarritoPort vaciarCarrito;

    public CarritoController(VerCarritoPort verCarrito,
                             AgregarItemCarritoPort agregarItem,
                             ActualizarItemCarritoPort actualizarItem,
                             QuitarItemCarritoPort quitarItem,
                             VaciarCarritoPort vaciarCarrito) {
        this.verCarrito = verCarrito;
        this.agregarItem = agregarItem;
        this.actualizarItem = actualizarItem;
        this.quitarItem = quitarItem;
        this.vaciarCarrito = vaciarCarrito;
    }

    // ── GET /api/carrito ────────────────────────────────────────────────────────

    @Operation(summary = "Ver carrito", description = "Devuelve los ítems del carrito con subtotales y total.")
    @GetMapping
    public ResponseEntity<CarritoResponse> ver(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        return ResponseEntity.ok(CarritoResponse.desdeDomain(verCarrito.ejecutar(usuario.id())));
    }

    // ── POST /api/carrito/items ─────────────────────────────────────────────────

    @Operation(summary = "Agregar ítem",
            description = "Agrega producto+talla al carrito. Si ya existe, suma la cantidad. Valida el stock sin reservarlo.")
    @PostMapping("/items")
    public ResponseEntity<CarritoResponse> agregar(@AuthenticationPrincipal UsuarioPrincipal usuario,
                                                   @Valid @RequestBody AgregarItemRequest request) {
        Carrito carrito = agregarItem.ejecutar(usuario.id(), request.productoId(), request.talla(), request.cantidad());
        return ResponseEntity.status(HttpStatus.CREATED).body(CarritoResponse.desdeDomain(carrito));
    }

    // ── PUT /api/carrito/items/{itemId} ─────────────────────────────────────────

    @Operation(summary = "Cambiar cantidad", description = "Reemplaza la cantidad de un ítem. Valida contra el stock disponible.")
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CarritoResponse> actualizar(@AuthenticationPrincipal UsuarioPrincipal usuario,
                                                      @PathVariable Long itemId,
                                                      @Valid @RequestBody ActualizarCantidadRequest request) {
        Carrito carrito = actualizarItem.ejecutar(usuario.id(), itemId, request.cantidad());
        return ResponseEntity.ok(CarritoResponse.desdeDomain(carrito));
    }

    // ── DELETE /api/carrito/items/{itemId} ──────────────────────────────────────

    @Operation(summary = "Quitar ítem", description = "Elimina un ítem del carrito.")
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> quitarItem(@AuthenticationPrincipal UsuarioPrincipal usuario,
                                           @PathVariable Long itemId) {
        quitarItem.ejecutar(usuario.id(), itemId);
        return ResponseEntity.noContent().build();
    }

    // ── DELETE /api/carrito ─────────────────────────────────────────────────────

    @Operation(summary = "Vaciar carrito", description = "Elimina todos los ítems del carrito.")
    @DeleteMapping
    public ResponseEntity<Void> vaciar(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        vaciarCarrito.ejecutar(usuario.id());
        return ResponseEntity.noContent().build();
    }

    // ── DTOs (Records) ──────────────────────────────────────────────────────────

    /** Request para agregar un ítem al carrito. */
    public record AgregarItemRequest(
            @NotNull(message = "El productoId es obligatorio")
            Long productoId,

            @NotBlank(message = "La talla es obligatoria")
            String talla,

            @NotNull(message = "La cantidad es obligatoria")
            @Min(value = 1, message = "La cantidad debe ser mayor a cero")
            Integer cantidad) {
    }

    /** Request para cambiar la cantidad de un ítem. */
    public record ActualizarCantidadRequest(
            @NotNull(message = "La cantidad es obligatoria")
            @Min(value = 1, message = "La cantidad debe ser mayor a cero")
            Integer cantidad) {
    }

    /** Response de un ítem del carrito con su subtotal. */
    public record CarritoItemResponse(
            Long itemId,
            Long productoTallaId,
            String nombreProducto,
            String talla,
            BigDecimal precioUnitario,
            int cantidad,
            BigDecimal subtotal) {

        static CarritoItemResponse desdeDomain(CarritoItem item) {
            return new CarritoItemResponse(
                    item.getId(),
                    item.getProductoTallaId(),
                    item.getNombreProducto(),
                    item.getTalla() != null ? item.getTalla().name() : null,
                    item.getPrecioUnitario(),
                    item.getCantidad(),
                    item.calcularSubtotal());
        }
    }

    /** Response completa del carrito con subtotales y total. */
    public record CarritoResponse(
            Long id,
            List<CarritoItemResponse> items,
            BigDecimal total,
            int totalUnidades,
            Instant actualizadoEn) {

        static CarritoResponse desdeDomain(Carrito carrito) {
            List<CarritoItemResponse> items = carrito.getItems().stream()
                    .map(CarritoItemResponse::desdeDomain)
                    .toList();
            return new CarritoResponse(
                    carrito.getId(),
                    items,
                    carrito.calcularTotal(),
                    carrito.contarTotalUnidades(),
                    carrito.getActualizadoEn());
        }
    }
}
