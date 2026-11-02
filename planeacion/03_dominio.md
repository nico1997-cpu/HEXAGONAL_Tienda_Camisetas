# 03 - Dominio (núcleo hexagonal)

Java puro, **sin anotaciones de Spring ni JPA**.

## Tareas
- [x] Modelos: `Producto`, `ProductoTalla`, `Talla` (enum), `Usuario`, `Rol` (enum), `Carrito`, `CarritoItem`, `Orden`, `OrdenItem`, `EstadoOrden`.
- [x] Reglas:
  - `ProductoTalla.descontarStock(cantidad)` → error si stock insuficiente.
  - `ProductoTalla.aumentarStock(cantidad)` → cantidad > 0.
  - `Carrito.agregarItem / actualizarCantidad / quitarItem / vaciar / calcularTotal`.
  - `Orden` se crea desde un carrito, calcula total con precio congelado.
- [x] Excepciones: `StockInsuficienteException`, `ProductoNoEncontradoException`, `CarritoVacioException`, `CredencialesInvalidasException`, etc.
- [x] Puertos de salida (`application/port/out`): `ProductoRepositoryPort`, `UsuarioRepositoryPort`, `CarritoRepositoryPort`, `OrdenRepositoryPort`, `PasswordEncoderPort`, `TokenProviderPort`.

## Pruebas
- [x] **Unitarias** (lo más fuerte de esta tarea): cada regla de negocio, casos felices y de error (32 pruebas pasando).
- [x] **Integración / Arquitectura**: test de arquitectura con **ArchUnit** que verifica que `domain` no depende de `infrastructure` ni de Spring.
