package com.camisastienda.infrastructure.adapter.in.web;

import com.camisastienda.application.port.out.TokenProviderPort;
import com.camisastienda.application.port.in.LoginPort;
import com.camisastienda.application.port.in.RegistrarUsuarioPort;
import com.camisastienda.domain.model.Usuario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * Controlador REST para los endpoints de autenticación: registro, login y perfil.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistrarUsuarioPort registrarUsuario;
    private final LoginPort login;
    private final TokenProviderPort tokenProvider;
    private final long expirationMs;

    public AuthController(RegistrarUsuarioPort registrarUsuario,
                          LoginPort login,
                          TokenProviderPort tokenProvider,
                          @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.registrarUsuario = registrarUsuario;
        this.login = login;
        this.tokenProvider = tokenProvider;
        this.expirationMs = expirationMs;
    }

    /** POST /api/auth/registro - Crea un nuevo usuario con rol CLIENTE. */
    @PostMapping("/registro")
    public ResponseEntity<UsuarioRegistradoResponse> registro(
            @Valid @RequestBody RegistroRequest request) {
        Usuario usuario = registrarUsuario.ejecutar(
                request.nombre(), request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UsuarioRegistradoResponse(
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getEmail(),
                        usuario.getRol().name(),
                        usuario.getCreadoEn()));
    }

    /** POST /api/auth/login - Autentica al usuario y devuelve el token JWT. */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = login.ejecutar(request.email(), request.password());
        String rol = tokenProvider.extraerRol(token);
        long expiraEn = System.currentTimeMillis() + expirationMs;
        return ResponseEntity.ok(new TokenResponse(token, "Bearer", expiraEn, rol));
    }

    /** GET /api/auth/me - Devuelve los datos del usuario autenticado. */
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(new MeResponse(email));
    }

    // ── Records de request/response ────────────────────────────────────────────

    public record RegistroRequest(
            @NotBlank(message = "El nombre es obligatorio")
            String nombre,
            @NotBlank(message = "El email es obligatorio")
            @Email(message = "El email no tiene un formato válido")
            String email,
            @NotBlank(message = "La contraseña es obligatoria")
            @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
            String password) {
    }

    public record LoginRequest(
            @NotBlank(message = "El email es obligatorio")
            @Email(message = "El email no tiene un formato válido")
            String email,
            @NotBlank(message = "La contraseña es obligatoria")
            String password) {
    }

    public record TokenResponse(String token, String tipo, long expiraEn, String rol) {
    }

    public record UsuarioRegistradoResponse(
            Long id, String nombre, String email, String rol, Instant creadoEn) {
    }

    public record MeResponse(String email) {
    }
}
