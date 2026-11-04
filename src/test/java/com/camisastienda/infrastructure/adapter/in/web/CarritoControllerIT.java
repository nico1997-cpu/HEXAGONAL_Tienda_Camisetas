package com.camisastienda.infrastructure.adapter.in.web;

import com.camisastienda.TestcontainersConfiguration;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración del carrito con MySQL real (Testcontainers).
 * Usa los datos semilla de V2: cliente cliente@camisastienda.com/cliente123,
 * admin admin@camisastienda.com/admin123, producto 1 (Real Madrid, $89.99, talla M con stock 20).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class CarritoControllerIT {

    private static final String CLIENTE_EMAIL = "cliente@camisastienda.com";
    private static final String CLIENTE_PASSWORD = "cliente123";
    private static final String ADMIN_EMAIL = "admin@camisastienda.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final Long PRODUCTO_ID = 1L;
    private static final String TALLA = "M";
    private static final double PRECIO = 89.99;

    @Autowired
    private MockMvc mockMvc;

    // ── Helpers ────────────────────────────────────────────────────────────────

    /** Hace login y devuelve el token JWT. */
    private String login(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }

    private String loginCliente() throws Exception {
        return login(CLIENTE_EMAIL, CLIENTE_PASSWORD);
    }

    /** Vacía el carrito del cliente para que cada prueba empiece limpia. */
    private void vaciarCarrito(String token) throws Exception {
        mockMvc.perform(delete("/api/carrito").header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    private MvcResult agregarItem(String token, long productoId, String talla, int cantidad) throws Exception {
        return mockMvc.perform(post("/api/carrito/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":%d,\"talla\":\"%s\",\"cantidad\":%d}"
                                .formatted(productoId, talla, cantidad)))
                .andReturn();
    }

    private String primerItemId(String token) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/carrito").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        return String.valueOf(JsonPath.read(result.getResponse().getContentAsString(), "$.items[0].itemId"));
    }

    // ── Flujo completo ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Flujo completo: ver → agregar → sumar duplicado → actualizar → quitar → vaciar")
    void flujoCompletoDelCarrito() throws Exception {
        String token = loginCliente();
        vaciarCarrito(token);

        // 1. GET: carrito vacío
        mockMvc.perform(get("/api/carrito").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.totalUnidades").value(0));

        // 2. POST: agrega 2 unidades → total 179.98
        mockMvc.perform(post("/api/carrito/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":%d,\"talla\":\"%s\",\"cantidad\":2}".formatted(PRODUCTO_ID, TALLA)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].nombreProducto").value("Camiseta Real Madrid Local 24/25"))
                .andExpect(jsonPath("$.items[0].talla").value("M"))
                .andExpect(jsonPath("$.items[0].cantidad").value(2))
                .andExpect(jsonPath("$.items[0].precioUnitario").value(PRECIO))
                .andExpect(jsonPath("$.items[0].subtotal").value(179.98))
                .andExpect(jsonPath("$.total").value(179.98));

        // 3. POST duplicado: suma cantidad en una sola línea → total 269.97
        mockMvc.perform(post("/api/carrito/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":%d,\"talla\":\"%s\",\"cantidad\":1}".formatted(PRODUCTO_ID, TALLA)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].cantidad").value(3))
                .andExpect(jsonPath("$.total").value(269.97))
                .andExpect(jsonPath("$.totalUnidades").value(3));

        // 4. PUT: cambia la cantidad a 5 → total 449.95
        String itemId = primerItemId(token);
        mockMvc.perform(put("/api/carrito/items/" + itemId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].cantidad").value(5))
                .andExpect(jsonPath("$.total").value(449.95));

        // 5. DELETE del ítem → carrito vacío
        mockMvc.perform(delete("/api/carrito/items/" + itemId).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/carrito").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());

        // 6. DELETE /api/carrito → vacía todo
        mockMvc.perform(post("/api/carrito/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":%d,\"talla\":\"%s\",\"cantidad\":1}".formatted(PRODUCTO_ID, TALLA)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/carrito").header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/carrito").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.total").value(0));
    }

    // ── Reglas de stock ────────────────────────────────────────────────────────

    @Test
    @DisplayName("No permite agregar más unidades que el stock disponible (409)")
    void agregar_superaStock_devuelve409() throws Exception {
        String token = loginCliente();
        vaciarCarrito(token);

        // Stock de la talla M del producto 1: 20 → pedir 21 debe fallar
        mockMvc.perform(post("/api/carrito/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":%d,\"talla\":\"%s\",\"cantidad\":21}".formatted(PRODUCTO_ID, TALLA)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").isNotEmpty());

        // Agrega 18 y luego intenta llegar a 21 vía PUT → también 409
        mockMvc.perform(post("/api/carrito/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":%d,\"talla\":\"%s\",\"cantidad\":18}".formatted(PRODUCTO_ID, TALLA)))
                .andExpect(status().isCreated());

        String itemId = primerItemId(token);
        mockMvc.perform(put("/api/carrito/items/" + itemId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\":21}"))
                .andExpect(status().isConflict());
    }

    // ── Aislamiento entre usuarios ─────────────────────────────────────────────

    @Test
    @DisplayName("Un usuario no ve ni manipula el carrito de otro")
    void carritoEsPrivadoPorUsuario() throws Exception {
        String tokenCliente = loginCliente();
        vaciarCarrito(tokenCliente);

        // El cliente semilla agrega un ítem
        mockMvc.perform(post("/api/carrito/items")
                        .header("Authorization", "Bearer " + tokenCliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":%d,\"talla\":\"%s\",\"cantidad\":2}".formatted(PRODUCTO_ID, TALLA)))
                .andExpect(status().isCreated());

        // Segundo usuario (CLIENTE recién registrado) con carrito vacío
        String emailNuevo = "carrito-it-" + System.currentTimeMillis() + "@test.com";
        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Otro Cliente\",\"email\":\"%s\",\"password\":\"clave123\"}"
                                .formatted(emailNuevo)))
                .andExpect(status().isCreated());
        String tokenOtro = login(emailNuevo, "clave123");

        // Ve su carrito vacío, no el del cliente semilla
        mockMvc.perform(get("/api/carrito").header("Authorization", "Bearer " + tokenOtro))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());

        // Y no puede manipular el ítem ajeno (404)
        String itemIdAjeno = primerItemId(tokenCliente);
        mockMvc.perform(put("/api/carrito/items/" + itemIdAjeno)
                        .header("Authorization", "Bearer " + tokenOtro)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\":1}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/carrito/items/" + itemIdAjeno)
                        .header("Authorization", "Bearer " + tokenOtro))
                .andExpect(status().isNotFound());

        // El carrito del cliente original sigue intacto
        mockMvc.perform(get("/api/carrito").header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].cantidad").value(2));

        vaciarCarrito(tokenCliente);
    }

    // ── Seguridad ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Sin token devuelve 401")
    void sinToken_devuelve401() throws Exception {
        mockMvc.perform(get("/api/carrito"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Con token de ADMIN devuelve 403 (el carrito es solo de CLIENTE)")
    void conAdmin_devuelve403() throws Exception {
        String tokenAdmin = login(ADMIN_EMAIL, ADMIN_PASSWORD);

        mockMvc.perform(get("/api/carrito").header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Request inválida (cantidad 0) devuelve 400 con detalle del campo")
    void agregar_cantidadInvalida_devuelve400() throws Exception {
        String token = loginCliente();
        vaciarCarrito(token);

        mockMvc.perform(post("/api/carrito/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":%d,\"talla\":\"%s\",\"cantidad\":0}".formatted(PRODUCTO_ID, TALLA)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isNotEmpty());
    }
}
