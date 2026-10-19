package com.example.demo.controller;

import com.example.demo.entity.Cliente;
import com.example.demo.repository.SolicitudRepository;
import com.example.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CONFLICT;

class UsuarioControllerTest {

    @Test
    void eliminarClienteConSolicitudesDevuelve409() {
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        SolicitudRepository solicitudRepository = mock(SolicitudRepository.class);
        UsuarioController controller = new UsuarioController(
                usuarioRepository, solicitudRepository);
        Long clienteId = 10L;

        when(usuarioRepository.findById(clienteId))
                .thenReturn(Optional.of(new Cliente("Ana", "ana@ejemplo.com", 0)));
        when(solicitudRepository.existsByClienteId(clienteId)).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.eliminar(clienteId));

        assertEquals(CONFLICT, exception.getStatusCode());
        assertEquals(
                "No se puede eliminar un cliente que tiene solicitudes",
                exception.getReason());
        verify(usuarioRepository, never()).delete(
                org.mockito.ArgumentMatchers.any());
    }
}
