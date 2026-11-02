package com.camisastienda.infrastructure.adapter.out.security;

import com.camisastienda.application.port.out.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adaptador que implementa PasswordEncoderPort usando BCrypt de Spring Security.
 * Delega toda la lógica en BCryptPasswordEncoder (factor de coste 12 por defecto).
 */
@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private final BCryptPasswordEncoder delegate;

    public BCryptPasswordEncoderAdapter() {
        this.delegate = new BCryptPasswordEncoder();
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return delegate.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return delegate.matches(rawPassword, encodedPassword);
    }
}
