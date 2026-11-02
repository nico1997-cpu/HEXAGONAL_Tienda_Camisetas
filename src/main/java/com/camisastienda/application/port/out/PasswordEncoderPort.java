package com.camisastienda.application.port.out;

/** Puerto de salida para encriptar y verificar contraseñas (ej. BCrypt). */
public interface PasswordEncoderPort {
    String encode(CharSequence rawPassword);
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
