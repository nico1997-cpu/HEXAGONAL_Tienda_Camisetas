# 03 - Dominio (núcleo hexagonal)

Java puro, **sin anotaciones de Spring ni JPA**.

## Tareas
- [ ] Modelos: `Producto`, `ProductoTalla`, `Talla` (enum), `Usuario`, `Rol` (enum), `Carrito`, `CarritoItem`, `Orden`, `OrdenItem`, `EstadoOrden`.
- [ ] Reglas:
  - `ProductoTalla.descontarStock(cantidad)` → error si stock insuficiente.
  - `ProductoTalla.aumentarStock(cantidad)` → cantidad > 0.
  - `Carrito.agregarItem / actualizarCantidad / quitarItem / vaciar / calcularTotal`.
  - `Orden` se crea desde un carrito, calcula total con precio congelado.
- [ ] Excepciones: `StockInsuficienteException`, `ProductoNoEncontradoException`, `CarritoVacioException`, `CredencialesInvalidasException`, etc.
- [ ] Puertos de salida (`application/port/out`): `ProductoRepositoryPort`, `UsuarioRepositoryPort`, `CarritoRepositoryPort`, `OrdenRepositoryPort`, `PasswordEncoderPort`, `TokenProviderPort`.

## Pruebas
- **Unitarias** (lo más fuerte de esta tarea): cada regla de negocio, casos felices y de error.
- **Integración**: test de arquitectura con **ArchUnit** que verifique que `domain` no depende de `infrastructure` ni de Spring.
