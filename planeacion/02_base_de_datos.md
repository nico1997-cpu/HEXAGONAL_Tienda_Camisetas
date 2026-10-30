# 02 - Base de datos (MySQL)

## Tareas
- [ ] Crear carpeta `database/` con `schema.sql` (script completo, independiente, ejecutable a mano en MySQL).
- [ ] Copiar/vincular como migración Flyway `src/main/resources/db/migration/V1__schema.sql`.
- [ ] `database/data.sql` con datos de ejemplo (camisetas, tallas, usuario admin) → `V2__datos_iniciales.sql`.

## Tablas
| Tabla | Campos principales |
|---|---|
| `usuarios` | id, nombre, email (único), password_hash, rol (ADMIN/CLIENTE), creado_en |
| `productos` | id, nombre, descripcion, equipo, liga, temporada, precio, imagen_url, activo, creado_en |
| `producto_tallas` | id, producto_id (FK), talla (XS,S,M,L,XL,XXL), stock, version — único (producto_id, talla) |
| `carritos` | id, usuario_id (FK, único), actualizado_en |
| `carrito_items` | id, carrito_id (FK), producto_talla_id (FK), cantidad |
| `ordenes` | id, usuario_id (FK), total, estado (CREADA/CANCELADA), creado_en |
| `orden_items` | id, orden_id (FK), producto_talla_id (FK), cantidad, precio_unitario |

Restricciones: `stock >= 0`, `cantidad > 0`, `precio >= 0` (CHECK).

## Pruebas
- **Integración**: Testcontainers levanta MySQL, Flyway migra sin errores; verificar que existen las tablas y los datos semilla; verificar que un CHECK rechaza stock negativo.
- **Unitarias**: no aplica lógica Java aquí (se documenta).
