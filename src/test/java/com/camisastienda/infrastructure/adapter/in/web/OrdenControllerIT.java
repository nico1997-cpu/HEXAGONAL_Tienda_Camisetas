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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración para Checkout y Órdenes con MySQL (Testcontainers).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class OrdenControllerIT {

    private static final String CLIENTE_EMAIL = "cliente@camisastienda.com";
    private static final String CLIENTE_PASSWORD = "cliente123";
    private static final String ADMIN_EMAIL = "admin@camisastienda.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final Long PRODUCTO_ID = 1L;
    private static final String TALLA = "M";

    @Autowired
    private MockMvc mockMvc;

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

    private void vaciarCarrito(String token) throws Exception {
        mockMvc.perform(delete("/api/carrito").header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    private void agregarAlCarrito(String token, long productoId, String talla, int cantidad) throws Exception {
        mockMvc.perform(post("/api/carrito/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":%d,\"talla\":\"%s\",\"cantidad\":%d}"
                                .formatted(productoId, talla, cantidad)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Flujo checkout feliz: agrega al carrito -> checkout -> orden creada -> carrito queda vacío")
    void checkout_flujoFeliz() throws Exception {
        String token = loginCliente();
        vaciarCarrito(token);
        agregarAlCarrito(token, PRODUCTO_ID, TALLA, 1);

        // Checkout
        MvcResult result = mockMvc.perform(post("/api/ordenes/checkout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.estado").value("CREADA"))
                .andExpect(jsonPath("$.total").value(89.99))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].talla").value(TALLA))
                .andReturn();

        Number ordenId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        // El carrito debe quedar vacío
        mockMvc.perform(get("/api/carrito").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());

        // Ver detalle de la orden recién creada
        mockMvc.perform(get("/api/ordenes/" + ordenId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ordenId))
                .andExpect(jsonPath("$.estado").value("CREADA"));
    }

    @Test
    @DisplayName("Checkout con carrito vacío devuelve 409 Conflict")
    void checkout_carritoVacio_devuelve409() throws Exception {
        String token = loginCliente();
        vaciarCarrito(token);

        mockMvc.perform(post("/api/ordenes/checkout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("Un cliente no puede ver la orden de otro cliente (404)")
    void ordenEsPrivadaPorUsuario() throws Exception {
        String tokenCliente1 = loginCliente();
        vaciarCarrito(tokenCliente1);
        agregarAlCarrito(tokenCliente1, PRODUCTO_ID, TALLA, 1);

        MvcResult result = mockMvc.perform(post("/api/ordenes/checkout")
                        .header("Authorization", "Bearer " + tokenCliente1))
                .andExpect(status().isCreated())
                .andReturn();
        Number ordenId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        // Registrar e iniciar sesión con otro cliente
        String emailNuevo = "orden-it-" + System.currentTimeMillis() + "@test.com";
        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Otro Cliente\",\"email\":\"%s\",\"password\":\"clave123\"}"
                                .formatted(emailNuevo)))
                .andExpect(status().isCreated());
        String tokenCliente2 = login(emailNuevo, "clave123");

        // Cliente 2 intenta consultar la orden de Cliente 1
        mockMvc.perform(get("/api/ordenes/" + ordenId).header("Authorization", "Bearer " + tokenCliente2))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Sin token devuelve 401")
    void sinToken_devuelve401() throws Exception {
        mockMvc.perform(get("/api/ordenes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Con token de ADMIN devuelve 403 (órdenes es solo de CLIENTE)")
    void conAdmin_devuelve403() throws Exception {
        String tokenAdmin = login(ADMIN_EMAIL, ADMIN_PASSWORD);

        mockMvc.perform(get("/api/ordenes").header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isForbidden());
    }
}

