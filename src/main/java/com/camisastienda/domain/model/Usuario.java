package com.camisastienda.domain.model;

import com.camisastienda.domain.exception.ReglaNegocioException;

import java.time.Instant;
import java.util.Objects;

public class Usuario {
    private Long id;
    private String nombre;
    private String email;
    private String passwordHash;
    private Rol rol;
    private Instant creadoEn;

    public Usuario() {
    }

    public Usuario(Long id, String nombre, String email, String passwordHash, Rol rol, Instant creadoEn) {
        validarDatos(nombre, email, rol);
        this.id = id;
        this.nombre = nombre.trim();
        this.email = email.trim().toLowerCase();
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
    }

    public static Usuario nuevo(String nombre, String email, String passwordHash, Rol rol) {
        return new Usuario(null, nombre, email, passwordHash, rol, Instant.now());
    }

    private void validarDatos(String nombre, String email, Rol rol) {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("El nombre del usuario no puede estar vacío");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ReglaNegocioException("El email del usuario no tiene un formato válido");
        }
        if (rol == null) {
            throw new ReglaNegocioException("El rol del usuario es obligatorio");
        }
    }

    public boolean esAdmin() {
        return Rol.ADMIN.equals(this.rol);
    }

    public boolean esCliente() {
        return Rol.CLIENTE.equals(this.rol);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return Objects.equals(id, usuario.id) || (email != null && email.equalsIgnoreCase(usuario.email));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}
