# 08 - Checkout y órdenes (sin pago real)

## Endpoints (CLIENTE)
| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/ordenes/checkout` | Crea orden desde el carrito |
| GET | `/api/ordenes` | Mis órdenes |
| GET | `/api/ordenes/{id}` | Detalle |

## Tareas
- [ ] `CheckoutUseCase` en **una transacción**: validar carrito no vacío → descontar stock de cada talla → crear orden con precio congelado → vaciar carrito.
- [ ] Si alguna talla no tiene stock suficiente: rollback total y 409 Conflict.
- [ ] Manejar `OptimisticLockException` (compra concurrente) → 409.

## Pruebas
- **Unitarias**: checkout feliz, carrito vacío, stock insuficiente (no se crea orden).
- **Integración**: checkout real en MySQL descuenta stock; rollback verificado; dos compras concurrentes del último artículo → solo una tiene éxito.
