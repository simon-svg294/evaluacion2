package com.example.demo.repository;

import com.example.demo.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreoAndIdNot(String correo, Long id);

    Page<Usuario> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
