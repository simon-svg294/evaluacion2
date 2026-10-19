package com.example.demo.model;

public class Cliente extends Usuario {

    public Cliente() {
    }

    public Cliente(Long id, String nombre, String correo) {
        super(id, nombre, correo);
    }

    @Override
    public String obtenerDescripcion() {
        return "Cliente";
    }
}
