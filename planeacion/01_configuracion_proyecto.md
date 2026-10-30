# 01 - Configuración del proyecto

## Tareas
- [ ] Crear proyecto Maven Spring Boot 3.x, Java 21 (`groupId: com.camisastienda`, `artifactId: camisas-tienda`).
- [ ] Dependencias: web, validation, data-jpa, security, mysql-connector-j, flyway-core + flyway-mysql, jjwt (api/impl/jackson), lombok, springdoc-openapi (Swagger).
- [ ] Dependencias de test: spring-boot-starter-test, spring-security-test, testcontainers (junit-jupiter, mysql).
- [ ] Crear estructura de paquetes hexagonal (ver `00_resumen_general.md`).
- [ ] `application.yml` con perfiles `dev`, `test`, `docker`; credenciales vía variables de entorno.
- [ ] Manejador global de errores (`@RestControllerAdvice`) con respuesta JSON estándar.
- [ ] Endpoint de salud (`/actuator/health`).

## Pruebas
- **Unitarias**: test del manejador de errores (mapea excepciones a códigos HTTP correctos).
- **Integración**: `contextLoads()` con Testcontainers MySQL; `/actuator/health` responde 200.

## Criterio de aceptación
`mvn verify` compila y pasa todos los tests.
