package com.camisastienda.application.usecase;

import com.camisastienda.application.port.out.PasswordEncoderPort;
import com.camisastienda.application.port.out.TokenProviderPort;
import com.camisastienda.application.port.out.UsuarioRepositoryPort;
import com.camisastienda.domain.exception.CredencialesInvalidasException;
import com.camisastienda.domain.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: autenticar usuario con email/contraseña y devolver un JWT.
 * Lanza CredencialesInvalidasException si el email no existe o la contraseña no coincide.
 */
@Service
@Transactional(readOnly = true)
public class LoginUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;

    public LoginUseCase(UsuarioRepositoryPort usuarioRepository,
                        PasswordEncoderPort passwordEncoder,
                        TokenProviderPort tokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public String ejecutar(String email, String passwordPlano) {
        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordEncoder.matches(passwordPlano, usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }

        return tokenProvider.generarToken(usuario);
    }
}
