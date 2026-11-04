package com.camisastienda.infrastructure.adapter.in.web;

import com.camisastienda.application.port.in.ActualizarProductoPort;
import com.camisastienda.application.port.in.AjustarStockPort;
import com.camisastienda.application.port.in.CrearProductoPort;
import com.camisastienda.application.port.in.EliminarProductoPort;
import com.camisastienda.application.port.in.ListarProductosPort;
import com.camisastienda.application.port.in.VerDetalleProductoPort;
import com.camisastienda.domain.model.Producto;
import com.camisastienda.domain.model.ProductoTalla;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Controlador REST para el catálogo de camisetas de fútbol.
 * - Lectura (GET): pública.
 * - Escritura (POST/PUT/DELETE/PATCH): solo ADMIN (regla en SecurityConfig).
 */
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Catálogo de camisetas de fútbol y gestión de stock")
public class ProductoController {

    private final ListarProductosPort listar;
    private final VerDetalleProductoPort verDetalle;
    private final CrearProductoPort crear;
    private final ActualizarProductoPort actualizar;
    private final EliminarProductoPort eliminar;
    private final AjustarStockPort ajustarStock;

    public ProductoController(ListarProductosPort listar,
                              VerDetalleProductoPort verDetalle,
                              CrearProductoPort crear,
                              ActualizarProductoPort actualizar,
                              EliminarProductoPort eliminar,
                              AjustarStockPort ajustarStock) {
        this.listar = listar;
        this.verDetalle = verDetalle;
        this.crear = crear;
        this.actualizar = actualizar;
        this.eliminar = eliminar;
        this.ajustarStock = ajustarStock;
    }

    // ── GET /api/productos ──────────────────────────────────────────────────────

    @Operation(summary = "Listar camisetas", description = "Filtra por equipo, liga, nombre o talla (todos opcionales). Solo muestra productos activos.")
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar(
            @Parameter(description = "Nombre del equipo (parcial)") @RequestParam(required = false) String equipo,
            @Parameter(description = "Liga (parcial)")              @RequestParam(required = false) String liga,
            @Parameter(description = "Nombre del producto (parcial)") @RequestParam(required = false) String nombre,
            @Parameter(description = "Talla: XS, S, M, L, XL, XXL") @RequestParam(required = false) String talla,
            @Parameter(description = "Número de página (0-indexed)") @RequestParam(defaultValue = "-1") int page,
            @Parameter(description = "Tamaño de página")             @RequestParam(defaultValue = "-1") int size) {

        List<ProductoResponse> respuesta = listar.ejecutar(equipo, liga, nombre, talla, page, size)
                .stream()
                .map(ProductoResponse::desdeProducto)
                .toList();
        return ResponseEntity.ok(respuesta);
    }

    // ── GET /api/productos/{id} ─────────────────────────────────────────────────

    @Operation(summary = "Detalle de camiseta", description = "Incluye la lista de tallas y stock disponible.")
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> detalle(@PathVariable Long id) {
        return ResponseEntity.ok(ProductoResponse.desdeProducto(verDetalle.ejecutar(id)));
    }

    // ── POST /api/productos ─────────────────────────────────────────────────────

    @Operation(summary = "Crear camiseta (ADMIN)", description = "Crea una nueva camiseta sin tallas. Las tallas se añaden con PATCH /tallas/{talla}/stock.")
    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        Producto creado = crear.ejecutar(
                request.nombre(), request.descripcion(), request.equipo(),
                request.liga(), request.temporada(), request.precio(), request.imagenUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductoResponse.desdeProducto(creado));
    }

    // ── PUT /api/productos/{id} ─────────────────────────────────────────────────

    @Operation(summary = "Actualizar camiseta (ADMIN)", description = "Actualiza los datos del producto. No modifica tallas ni stock.")
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody ProductoRequest request) {
        Producto actualizado = actualizar.ejecutar(
                id, request.nombre(), request.descripcion(), request.equipo(),
                request.liga(), request.temporada(), request.precio(), request.imagenUrl());
        return ResponseEntity.ok(ProductoResponse.desdeProducto(actualizado));
    }

    // ── DELETE /api/productos/{id} ──────────────────────────────────────────────

    @Operation(summary = "Eliminar camiseta (ADMIN)", description = "Borrado lógico: marca activo=false. El registro se conserva para historial de órdenes.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        eliminar.ejecutar(id);
        return ResponseEntity.noContent().build();
    }

    // ── PATCH /api/productos/{id}/tallas/{talla}/stock ──────────────────────────

    @Operation(summary = "Ajustar stock (ADMIN)", description = "Establece el nuevo stock para una talla. Si la talla no existía la crea.")
    @PatchMapping("/{id}/tallas/{talla}/stock")
    public ResponseEntity<TallaStockResponse> ajustarStock(
            @PathVariable Long id,
            @PathVariable String talla,
            @Valid @RequestBody StockRequest request) {
        ProductoTalla pt = ajustarStock.ejecutar(id, talla, request.stock());
        return ResponseEntity.ok(TallaStockResponse.desdeDomain(pt));
    }

    // ── DTOs (Records) ──────────────────────────────────────────────────────────

    /** Request para crear o actualizar un producto. */
    public record ProductoRequest(
            @NotBlank(message = "El nombre es obligatorio")
            String nombre,

            String descripcion,

            @NotBlank(message = "El equipo es obligatorio")
            String equipo,

            @NotBlank(message = "La liga es obligatoria")
            String liga,

            @NotBlank(message = "La temporada es obligatoria")
            String temporada,

            @NotNull(message = "El precio es obligatorio")
            @DecimalMin(value = "0.0", message = "El precio debe ser mayor o igual a 0")
            BigDecimal precio,

            String imagenUrl) {
    }

    /** Request para ajustar el stock de una talla. */
    public record StockRequest(
            @Min(value = 0, message = "El stock no puede ser negativo")
            int stock) {
    }

    /** Response de detalle de una talla con stock. */
    public record TallaStockResponse(Long id, Long productoId, String talla, int stock) {
        static TallaStockResponse desdeDomain(ProductoTalla pt) {
            return new TallaStockResponse(pt.getId(), pt.getProductoId(), pt.getTalla().name(), pt.getStock());
        }
    }

    /** Response completa de un producto con sus tallas. */
    public record ProductoResponse(
            Long id,
            String nombre,
            String descripcion,
            String equipo,
            String liga,
            String temporada,
            BigDecimal precio,
            String imagenUrl,
            boolean activo,
            List<TallaStockResponse> tallas,
            Instant creadoEn) {

        static ProductoResponse desdeProducto(Producto p) {
            List<TallaStockResponse> tallasResp = p.getTallas().stream()
                    .map(TallaStockResponse::desdeDomain)
                    .toList();
            return new ProductoResponse(
                    p.getId(), p.getNombre(), p.getDescripcion(), p.getEquipo(), p.getLiga(),
                    p.getTemporada(), p.getPrecio(), p.getImagenUrl(), p.isActivo(),
                    tallasResp, p.getCreadoEn());
        }
    }
}
