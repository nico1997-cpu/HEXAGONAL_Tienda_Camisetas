# 01 - Configuración del proyecto

## Tareas
- [x] Crear proyecto Maven Spring Boot 3.x, Java 21 (`groupId: com.camisastienda`, `artifactId: camisas-tienda`).
- [x] Dependencias: web, validation, data-jpa, security, mysql-connector-j, flyway-core + flyway-mysql, jjwt (api/impl/jackson), lombok, springdoc-openapi (Swagger).
- [x] Dependencias de test: spring-boot-starter-test, spring-security-test, testcontainers (junit-jupiter, mysql).
- [x] Crear estructura de paquetes hexagonal (ver `00_resumen_general.md`).
- [x] `application.yml` con perfiles `dev`, `test`, `docker`; credenciales vía variables de entorno.
- [x] Manejador global de errores (`@RestControllerAdvice`) con respuesta JSON estándar.
- [x] Endpoint de salud (`/actuator/health`).

## Pruebas
- **Unitarias**: test del manejador de errores (mapea excepciones a códigos HTTP correctos).
- **Integración**: `contextLoads()` con Testcontainers MySQL; `/actuator/health` responde 200.

## Criterio de aceptación
`mvn verify` compila y pasa todos los tests.
