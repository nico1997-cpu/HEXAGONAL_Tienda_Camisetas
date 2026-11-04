package com.camisastienda.application.usecase;

import com.camisastienda.application.port.in.RegistrarUsuarioPort;
import com.camisastienda.application.port.out.PasswordEncoderPort;
import com.camisastienda.application.port.out.UsuarioRepositoryPort;
import com.camisastienda.domain.exception.ReglaNegocioException;
import com.camisastienda.domain.model.Rol;
import com.camisastienda.domain.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: registrar un nuevo usuario con rol CLIENTE.
 * Valida que el email no exista y guarda la contraseña hasheada.
 */
@Service
@Transactional
public class RegistrarUsuarioUseCase implements RegistrarUsuarioPort {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;

    public RegistrarUsuarioUseCase(UsuarioRepositoryPort usuarioRepository,
                                   PasswordEncoderPort passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario ejecutar(String nombre, String email, String passwordPlano) {
        if (usuarioRepository.existePorEmail(email)) {
            throw new ReglaNegocioException("Ya existe un usuario registrado con el email: " + email);
        }
        String hash = passwordEncoder.encode(passwordPlano);
        Usuario nuevo = Usuario.nuevo(nombre, email, hash, Rol.CLIENTE);
        return usuarioRepository.guardar(nuevo);
    }
}
