package com.example.demo.dto;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UsuarioRequestTest {

    @Test
    void empleadoSinCargoDevuelveError400() {
        UsuarioRequest request = new UsuarioRequest(
                UsuarioRequest.TipoUsuario.EMPLEADO,
                "Ana",
                "ana@ejemplo.com",
                null,
                " ",
                2500.0,
                null);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                request::toEntity);

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void empleadoSinSalarioDevuelveError400() {
        UsuarioRequest request = new UsuarioRequest(
                UsuarioRequest.TipoUsuario.EMPLEADO,
                "Ana",
                "ana@ejemplo.com",
                null,
                "Asesora",
                null,
                null);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                request::toEntity);

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
}
