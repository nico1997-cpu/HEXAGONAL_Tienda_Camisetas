package com.camisastienda.application.port.in;

/** Puerto de entrada: autenticar a un usuario y obtener su token. */
public interface LoginPort {

    String ejecutar(String email, String passwordPlano);
}
