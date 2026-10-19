package com.example.demo.dto;

import com.example.demo.entity.Administrador;
import com.example.demo.entity.Cliente;
import com.example.demo.entity.Empleado;
import com.example.demo.entity.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public record UsuarioRequest(
        @NotNull TipoUsuario tipo,
        @NotBlank String nombre,
        @NotBlank @Email String correo,
        @Min(1) @Max(5) Integer nivelAcceso,
        String cargo,
        @Positive Double salario,
        @Min(0) Integer puntosFidelidad) {

    public enum TipoUsuario {
        ADMINISTRADOR,
        EMPLEADO,
        CLIENTE
    }

    public Usuario toEntity() {
        return switch (tipo) {
            case ADMINISTRADOR -> {
                if (nivelAcceso == null) {
                    throw badRequest("nivelAcceso es obligatorio para un administrador");
                }
                yield new Administrador(nombre, correo, nivelAcceso);
            }
            case EMPLEADO -> {
                if (cargo == null || cargo.isBlank() || salario == null) {
                    throw badRequest("cargo y salario son obligatorios para un empleado");
                }
                yield new Empleado(nombre, correo, cargo, salario);
            }
            case CLIENTE -> new Cliente(nombre, correo,
                    puntosFidelidad == null ? 0 : puntosFidelidad);
        };
    }

    private static ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    public boolean matches(Usuario usuario) {
        return switch (tipo) {
            case ADMINISTRADOR -> usuario instanceof Administrador;
            case EMPLEADO -> usuario instanceof Empleado;
            case CLIENTE -> usuario instanceof Cliente;
        };
    }
}
