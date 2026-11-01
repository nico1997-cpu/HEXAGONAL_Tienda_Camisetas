package com.camisastienda.infrastructure.adapter.in.web.error;

import com.camisastienda.domain.exception.RecursoNoEncontradoException;
import com.camisastienda.domain.exception.ReglaNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest("GET", "/api/prueba");
    }

    @Test
    void recursoNoEncontrado_devuelve404() {
        ResponseEntity<ErrorResponse> r = handler.handleNotFound(new RecursoNoEncontradoException("No existe"), request);
        assertThat(r.getStatusCode().value()).isEqualTo(404);
        assertThat(r.getBody().message()).isEqualTo("No existe");
        assertThat(r.getBody().path()).isEqualTo("/api/prueba");
    }

    @Test
    void reglaNegocio_devuelve409() {
        ResponseEntity<ErrorResponse> r = handler.handleBusiness(new ReglaNegocioException("Sin stock"), request);
        assertThat(r.getStatusCode().value()).isEqualTo(409);
        assertThat(r.getBody().message()).isEqualTo("Sin stock");
    }

    @Test
    void validacion_devuelve400ConErroresPorCampo() throws Exception {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(new Object(), "dto");
        br.rejectValue(null, "x"); // error global (ignorado en fieldErrors)
        br.addError(new org.springframework.validation.FieldError("dto", "nombre", "es obligatorio"));
        MethodParameter mp = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("dummy", String.class), 0);

        ResponseEntity<ErrorResponse> r = handler.handleValidation(new MethodArgumentNotValidException(mp, br), request);

        assertThat(r.getStatusCode().value()).isEqualTo(400);
        assertThat(r.getBody().fieldErrors()).containsEntry("nombre", "es obligatorio");
    }

    @Test
    void cuerpoMalFormado_devuelve400() {
        var ex = new HttpMessageNotReadableException("bad", new ServletServerHttpRequest(request));
        assertThat(handler.handleUnreadable(ex, request).getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void errorGenerico_devuelve500SinFiltrarDetalles() {
        ResponseEntity<ErrorResponse> r = handler.handleGeneric(new IllegalStateException("secreto"), request);
        assertThat(r.getStatusCode().value()).isEqualTo(500);
        assertThat(r.getBody().message()).doesNotContain("secreto");
    }

    @SuppressWarnings("unused")
    private void dummy(String arg) {
    }
}
