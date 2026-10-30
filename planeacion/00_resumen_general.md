# 00 - Resumen general del proyecto

## Objetivo
Backend de muestra para una tienda de camisetas deportivas de fútbol: catálogo, stock, carrito, checkout simple (sin pago real) y login con JWT.

## Stack tecnológico
| Elemento | Elección |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.x |
| Build | Maven |
| Base de datos | MySQL 8 |
| Persistencia | **ORM: JPA / Hibernate (Spring Data JPA)** |
| Migraciones | Flyway (usa el script de `database/`) |
| Seguridad | Spring Security + JWT |
| Tests | JUnit 5, Mockito, Spring Boot Test, Testcontainers (MySQL) |
| Contenedores | Docker + docker-compose |

## ¿Por qué ORM (JPA/Hibernate) y no ODM?
- **ODM** (Object Document Mapper) es para bases de **documentos** (MongoDB). Nuestra BD es **MySQL (relacional)**, así que lo correcto es un **ORM**.
- El modelo es naturalmente relacional: Usuario → Carrito → Items → Producto/Talla, Órdenes con detalle. Hay relaciones y claves foráneas.
- Necesitamos **transacciones ACID** en el checkout (crear orden + descontar stock de forma atómica). JPA + `@Transactional` lo resuelve, y con `@Version` (bloqueo optimista) evitamos vender stock que no existe.
- Spring Data JPA es el estándar en Spring Boot: menos código, mucha documentación.
- En arquitectura hexagonal las entidades JPA quedan **solo en infraestructura**; el dominio no depende de Hibernate.

## Arquitectura hexagonal (puertos y adaptadores)
```
src/main/java/com/camisastienda/
├── domain/                 # Núcleo: modelos y reglas de negocio puras (sin Spring)
│   ├── model/              # Producto, Talla, Usuario, Carrito, Orden...
│   └── exception/
├── application/            # Casos de uso
│   ├── port/in/            # Puertos de entrada (interfaces de casos de uso)
│   ├── port/out/           # Puertos de salida (repositorios, token, password)
│   └── service/            # Implementación de casos de uso
└── infrastructure/         # Adaptadores
    ├── adapter/in/web/     # Controladores REST + DTOs
    ├── adapter/out/persistence/  # Entidades JPA, repos Spring Data, mappers
    ├── security/           # JWT, filtros, config Spring Security
    └── config/
```

## Roles
- **ADMIN**: crea/edita/elimina productos y gestiona stock.
- **CLIENTE**: ve catálogo, usa carrito, hace checkout.

## Modelo mínimo de Producto
`id, nombre, descripcion, equipo, liga, temporada, precio, imagenUrl, activo` y stock **por talla** (`talla`, `stock`).
> La "cantidad" se usa en el ítem del carrito/orden (cuántas unidades compra el cliente); el "stock" es lo disponible por talla.

## Lista de tareas
| # | Archivo | Tema |
|---|---|---|
| 01 | `01_configuracion_proyecto.md` | Esqueleto Maven + estructura hexagonal |
| 02 | `02_base_de_datos.md` | Script MySQL aparte + Flyway |
| 03 | `03_dominio.md` | Modelos y reglas de negocio |
| 04 | `04_persistencia_jpa.md` | Adaptadores de persistencia (ORM) |
| 05 | `05_seguridad_jwt_login.md` | Registro, login, JWT, roles |
| 06 | `06_productos_y_stock.md` | Catálogo (visión de camisetas) y stock |
| 07 | `07_carrito_compras.md` | Carrito |
| 08 | `08_checkout_ordenes.md` | Checkout y órdenes |
| 09 | `09_dockerizacion.md` | Dockerfile + docker-compose |
| 10 | `10_readme_despliegue.md` | README con manual de despliegue |

## Regla obligatoria para TODAS las tareas
Una tarea **no se considera terminada** hasta que:
1. Tiene **pruebas unitarias** (JUnit 5 + Mockito) de lo creado.
2. Tiene **pruebas de integración** (Spring Boot Test + Testcontainers MySQL / MockMvc).
3. `mvn verify` pasa en verde.
