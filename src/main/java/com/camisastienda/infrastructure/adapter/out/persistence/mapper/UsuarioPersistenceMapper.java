package com.camisastienda.infrastructure.adapter.out.persistence.mapper;

import com.camisastienda.domain.model.Rol;
import com.camisastienda.domain.model.Usuario;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

/** Convierte entre Usuario (dominio) y UsuarioEntity (base de datos). */
@Component
public class UsuarioPersistenceMapper {

    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;
        return new Usuario(
                entity.getId(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getPasswordHash(),
                Rol.desdeTexto(entity.getRol()),
                entity.getCreadoEn()
        );
    }

    public UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) return null;
        return new UsuarioEntity(
                domain.getId(),
                domain.getNombre(),
                domain.getEmail(),
                domain.getPasswordHash(),
                domain.getRol().name(),
                domain.getCreadoEn()
        );
    }
}
