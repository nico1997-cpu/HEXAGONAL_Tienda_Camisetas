# 05 - Seguridad: login, registro y JWT

## Endpoints
| Método | Ruta | Acceso |
|---|---|---|
| POST | `/api/auth/registro` | Público (crea CLIENTE) |
| POST | `/api/auth/login` | Público → devuelve `{ token, tipo, expiraEn, rol }` |
| GET | `/api/auth/me` | Autenticado |

## Tareas
- [ ] Casos de uso `RegistrarUsuarioUseCase`, `LoginUseCase`.
- [ ] Adaptador `BCryptPasswordEncoderAdapter` (implementa `PasswordEncoderPort`).
- [ ] Adaptador `JwtTokenProvider` (jjwt, HS256, secreto y expiración por variable de entorno `JWT_SECRET`, `JWT_EXPIRATION`).
- [ ] `JwtAuthenticationFilter` + `SecurityConfig` stateless.
- [ ] Reglas: `GET /api/productos/**` público; `POST/PUT/DELETE /api/productos/**` solo ADMIN; `/api/carrito/**` y `/api/ordenes/**` CLIENTE.
- [ ] Respuestas 401/403 en JSON.

## Pruebas
- **Unitarias**: generar/validar/expirar token; casos de uso de login (credenciales malas, email duplicado).
- **Integración** (MockMvc + Testcontainers): registro → login → acceso con token; sin token 401; CLIENTE en endpoint ADMIN 403.
