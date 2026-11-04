# 09 - Dockerización

## Tareas
- [x] `Dockerfile` multi-stage (Maven + JDK 21 para compilar → JRE 21 para ejecutar, usuario no root).
- [x] `.dockerignore`.
- [x] `docker-compose.yml`: servicios `mysql` (8, volumen, healthcheck) y `api` (depende de mysql healthy, perfil `docker`).
- [x] `.env.example` con `MYSQL_*`, `JWT_SECRET`, `JWT_EXPIRATION`.

## Pruebas
- [x] **Unitarias**: se mantienen las existentes (`mvn verify` dentro del build).
- [x] **Integración**: `docker compose up -d` → `/actuator/health` = UP → login con admin semilla y listar productos (script de humo `scripts/smoke-test.ps1` / `.sh`).
