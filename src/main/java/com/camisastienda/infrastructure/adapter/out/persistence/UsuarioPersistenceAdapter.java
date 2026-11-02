package com.camisastienda.infrastructure.adapter.out.persistence;

import com.camisastienda.application.port.out.UsuarioRepositoryPort;
import com.camisastienda.domain.model.Usuario;
import com.camisastienda.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import com.camisastienda.infrastructure.adapter.out.persistence.mapper.UsuarioPersistenceMapper;
import com.camisastienda.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository repository;
    private final UsuarioPersistenceMapper mapper;

    public UsuarioPersistenceAdapter(UsuarioJpaRepository repository, UsuarioPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmailIgnoreCase(email).map(mapper::toDomain);
    }

    @Override
    public boolean existePorEmail(String email) {
        return repository.existsByEmailIgnoreCase(email);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = mapper.toEntity(usuario);
        UsuarioEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }
}
