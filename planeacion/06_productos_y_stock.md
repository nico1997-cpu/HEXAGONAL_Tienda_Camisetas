# 06 - Productos (visión de camisetas) y stock

## Endpoints
| Método | Ruta | Acceso |
|---|---|---|
| GET | `/api/productos?equipo=&liga=&nombre=&talla=&page=&size=` | Público |
| GET | `/api/productos/{id}` | Público (detalle con tallas y stock) |
| POST | `/api/productos` | ADMIN |
| PUT | `/api/productos/{id}` | ADMIN |
| DELETE | `/api/productos/{id}` | ADMIN (borrado lógico `activo=false`) |
| PATCH | `/api/productos/{id}/tallas/{talla}/stock` | ADMIN (ajustar stock) |

## Tareas
- [ ] Casos de uso: listar, ver detalle, crear, actualizar, eliminar, ajustar stock.
- [ ] DTOs con Bean Validation (nombre obligatorio, precio ≥ 0, stock ≥ 0, talla válida).
- [ ] Controlador REST + documentación Swagger.

## Pruebas
- **Unitarias**: servicios con puertos mockeados (crear, stock negativo rechazado, producto inexistente).
- **Integración**: MockMvc + MySQL: CRUD completo como ADMIN, listado/filtros públicos, validaciones 400.
