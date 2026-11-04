# 07 - Carrito de compras

Un carrito por usuario, persistido en BD.

## Endpoints (CLIENTE)
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/carrito` | Ver carrito con subtotales y total |
| POST | `/api/carrito/items` | `{ productoId, talla, cantidad }` |
| PUT | `/api/carrito/items/{itemId}` | Cambiar cantidad |
| DELETE | `/api/carrito/items/{itemId}` | Quitar ítem |
| DELETE | `/api/carrito` | Vaciar |

## Tareas
- [x] Casos de uso del carrito.
- [x] Validar que la cantidad no supere el stock disponible al agregar (el stock **no** se reserva aquí, se descuenta en checkout).
- [x] Si el mismo producto+talla ya existe, sumar cantidad.

## Pruebas
- [x] **Unitarias**: agregar, sumar duplicados, superar stock, total correcto.
- [x] **Integración**: flujo completo con token de CLIENTE; un usuario no ve el carrito de otro.
