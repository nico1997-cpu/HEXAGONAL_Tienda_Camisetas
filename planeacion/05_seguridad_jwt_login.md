# 05 - Seguridad: login, registro y JWT

## Endpoints
| Método | Ruta | Acceso |
|---|---|---|
| POST | `/api/auth/registro` | Público (crea CLIENTE) |
| POST | `/api/auth/login` | Público → devuelve `{ token, tipo, expiraEn, rol }` |
| GET | `/api/auth/me` | Autenticado |

## Tareas
- [x] Casos de uso `RegistrarUsuarioUseCase`, `LoginUseCase`.
- [x] Adaptador `BCryptPasswordEncoderAdapter` (implementa `PasswordEncoderPort`).
- [x] Adaptador `JwtTokenProvider` (jjwt, HS256, secreto y expiración por variable de entorno `JWT_SECRET`, `JWT_EXPIRATION`).
- [x] `JwtAuthenticationFilter` + `SecurityConfig` stateless.
- [x] Reglas: `GET /api/productos/**` público; `POST/PUT/DELETE /api/productos/**` solo ADMIN; `/api/carrito/**` y `/api/ordenes/**` CLIENTE.
- [x] Respuestas 401/403 en JSON.

## Pruebas
- [x] **Unitarias**: generar/validar/expirar token; casos de uso de login (credenciales malas, email duplicado).
- [x] **Integración** (MockMvc + Testcontainers): registro → login → acceso con token; sin token 401; CLIENTE en endpoint ADMIN 403.
