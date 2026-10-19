package com.example.demo.model;

public class Empleado extends Usuario {

    public Empleado() {
    }

    public Empleado(Long id, String nombre, String correo) {
        super(id, nombre, correo);
    }

    @Override
    public String obtenerDescripcion() {
        return "Empleado";
    }
}
