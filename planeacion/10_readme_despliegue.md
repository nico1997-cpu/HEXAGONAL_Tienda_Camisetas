# 10 - README y manual de despliegue

## Contenido del `README.md`
- [x] Descripción del proyecto y stack.
- [x] Diagrama de la arquitectura hexagonal.
- [x] **Manual de despliegue**:
  1. Requisitos (Java 21, Maven, Docker, MySQL 8 si es local).
  2. Despliegue con Docker (`cp .env.example .env` → `docker compose up -d --build`).
  3. Despliegue local sin Docker (crear BD con `database/schema.sql`, variables de entorno, `mvn spring-boot:run`).
  4. Variables de entorno (tabla).
  5. Usuario admin por defecto.
  6. Verificación (`/actuator/health`, Swagger en `/swagger-ui.html`).
  7. Solución de problemas comunes (puerto 3306 ocupado, etc.).
- [x] Cómo ejecutar tests (`mvn test`, `mvn verify`; requiere Docker para Testcontainers).
- [x] Tabla de endpoints y ejemplos `curl`.

## Pruebas
- [x] **Integración**: seguir el manual paso a paso en limpio y confirmar que funciona (checklist).
