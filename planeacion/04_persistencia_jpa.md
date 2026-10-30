# 04 - Persistencia con ORM (JPA / Hibernate)

## Tareas
- [ ] Entidades JPA en `infrastructure/adapter/out/persistence/entity` (`UsuarioEntity`, `ProductoEntity`, `ProductoTallaEntity` con `@Version`, `CarritoEntity`, `CarritoItemEntity`, `OrdenEntity`, `OrdenItemEntity`).
- [ ] Repositorios Spring Data (`JpaRepository`).
- [ ] Mappers Entidad ↔ Dominio (manuales o MapStruct).
- [ ] Adaptadores que implementan los puertos de salida (`ProductoPersistenceAdapter`, etc.).
- [ ] `spring.jpa.hibernate.ddl-auto=validate` (el esquema lo crea Flyway).
- [ ] Consultas: productos por equipo/liga/nombre con paginación; tallas con stock > 0.

## Pruebas
- **Unitarias**: mappers (dominio ↔ entidad) y adaptadores con repos mockeados.
- **Integración**: `@DataJpaTest` + Testcontainers MySQL: guardar/buscar/filtrar/paginar; bloqueo optimista (`@Version`) lanza excepción en actualización concurrente.
