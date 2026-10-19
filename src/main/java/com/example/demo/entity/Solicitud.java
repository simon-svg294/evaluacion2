package com.example.demo.entity;

public class Solicitud {

    public enum Estado {
        PENDIENTE,
        PROCESADA,
        RECHAZADA
    }

    private String descripcion;
    private Estado estado = Estado.PENDIENTE;

    public Solicitud() {
    }

    public Solicitud(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        this.estado = estado;
    }
}
