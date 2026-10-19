package com.example.demo.model;

public class Administrador extends Usuario {

    public Administrador() {
    }

    public Administrador(Long id, String nombre, String correo) {
        super(id, nombre, correo);
    }

    @Override
    public String obtenerDescripcion() {
        return "Administrador";
    }
}
