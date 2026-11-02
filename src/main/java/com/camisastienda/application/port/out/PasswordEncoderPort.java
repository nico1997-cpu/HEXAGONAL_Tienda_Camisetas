package com.camisastienda.application.port.out;

public interface PasswordEncoderPort {
    String encode(CharSequence rawPassword);
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
