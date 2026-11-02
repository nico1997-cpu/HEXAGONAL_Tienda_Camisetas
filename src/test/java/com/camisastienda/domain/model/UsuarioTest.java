package com.camisastienda.domain.model;

import com.camisastienda.domain.exception.ReglaNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioTest {

    @Test
    @DisplayName("Debe crear usuario válido correctamente")
    void crearUsuario_datosValidos_creaExitosamente() {
        Usuario u = Usuario.nuevo("Juan Perez", "juan@test.com", "hash123", Rol.CLIENTE);

        assertThat(u.getNombre()).isEqualTo("Juan Perez");
        assertThat(u.getEmail()).isEqualTo("juan@test.com");
        assertThat(u.esCliente()).isTrue();
        assertThat(u.esAdmin()).isFalse();
    }

    @Test
    @DisplayName("Debe fallar al crear usuario con email inválido o nombre vacío")
    void crearUsuario_datosInvalidos_lanzaExcepcion() {
        assertThatThrownBy(() -> Usuario.nuevo("", "test@test.com", "hash", Rol.CLIENTE))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("nombre");

        assertThatThrownBy(() -> Usuario.nuevo("Juan", "email-invalido", "hash", Rol.CLIENTE))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("email");

        assertThatThrownBy(() -> Usuario.nuevo("Juan", "juan@test.com", "hash", null))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("rol");
    }
}
