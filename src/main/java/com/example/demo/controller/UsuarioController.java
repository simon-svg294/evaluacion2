package com.example.demo.controller;

import com.example.demo.dto.UsuarioRequest;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public Page<Usuario> listar(
            @PageableDefault(size = 10, sort = "nombre",
                    direction = Sort.Direction.ASC) Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    @GetMapping("/buscar")
    public Page<Usuario> buscarPorNombre(
            @RequestParam String nombre,
            @PageableDefault(size = 10, sort = "nombre",
                    direction = Sort.Direction.ASC) Pageable pageable) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    @GetMapping("/{id}")
    public Usuario buscarPorId(@PathVariable Long id) {
        return buscarUsuario(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario crear(@Valid @RequestBody UsuarioRequest request) {
        if (usuarioRepository.findByCorreo(request.correo()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe un usuario con ese correo");
        }
        return usuarioRepository.save(request.toEntity());
    }

    @PutMapping("/{id}")
    public Usuario actualizar(@PathVariable Long id,
                              @Valid @RequestBody UsuarioRequest request) {
        Usuario existente = buscarUsuario(id);
        if (!request.matches(existente)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de usuario no puede cambiar durante la actualización");
        }
        if (usuarioRepository.existsByCorreoAndIdNot(request.correo(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe un usuario con ese correo");
        }

        Usuario actualizado = request.toEntity();
        actualizado.setId(id);
        return usuarioRepository.save(actualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        Usuario usuario = buscarUsuario(id);
        usuarioRepository.delete(usuario);
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe un usuario con ese id"));
    }
}
