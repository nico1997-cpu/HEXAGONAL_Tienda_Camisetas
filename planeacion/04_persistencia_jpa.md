# 04 - Persistencia con ORM (JPA / Hibernate)

## Tareas
- [x] Entidades JPA en `infrastructure/adapter/out/persistence/entity` (`UsuarioEntity`, `ProductoEntity`, `ProductoTallaEntity` con `@Version`, `CarritoEntity`, `CarritoItemEntity`, `OrdenEntity`, `OrdenItemEntity`).
- [x] Repositorios Spring Data (`JpaRepository`).
- [x] Mappers Entidad ↔ Dominio (manuales o MapStruct).
- [x] Adaptadores que implementan los puertos de salida (`ProductoPersistenceAdapter`, etc.).
- [x] `spring.jpa.hibernate.ddl-auto=validate` (el esquema lo crea Flyway).
- [x] Consultas: productos por equipo/liga/nombre con paginación; tallas con stock > 0.

## Pruebas
- [x] **Unitarias**: mappers (dominio ↔ entidad) y adaptadores con repos mockeados.
- [x] **Integración**: `@DataJpaTest` + Testcontainers MySQL: guardar/buscar/filtrar/paginar; bloqueo optimista (`@Version`) lanza excepción en actualización concurrente (`PersistenciaJpaIT`).
