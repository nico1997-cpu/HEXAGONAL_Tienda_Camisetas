# 09 - Dockerización

## Tareas
- [ ] `Dockerfile` multi-stage (Maven + JDK 21 para compilar → JRE 21 para ejecutar, usuario no root).
- [ ] `.dockerignore`.
- [ ] `docker-compose.yml`: servicios `mysql` (8, volumen, healthcheck) y `api` (depende de mysql healthy, perfil `docker`).
- [ ] `.env.example` con `MYSQL_*`, `JWT_SECRET`, `JWT_EXPIRATION`.

## Pruebas
- **Unitarias**: se mantienen las existentes (`mvn verify` dentro del build).
- **Integración**: `docker compose up -d` → `/actuator/health` = UP → login con admin semilla y listar productos (script de humo `scripts/smoke-test.ps1` / `.sh`).
