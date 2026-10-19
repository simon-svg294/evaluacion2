package com.example.demo.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CLIENTE")
class Cliente extends Usuario {

    private Integer puntosFidelidad = 0;

    public Cliente() {
    }

    public Cliente(String nombre, String correo,
                   Integer puntosFidelidad) {
        super(nombre, correo);
        this.puntosFidelidad = puntosFidelidad;
    }

    public Integer getPuntosFidelidad() {
        return puntosFidelidad;
    }

    public void setPuntosFidelidad(Integer puntosFidelidad) {
        this.puntosFidelidad = puntosFidelidad;
    }

    @Override
    public String obtenerDescripcion() {
        return "Cliente con " + puntosFidelidad
                + " puntos de fidelidad";
    }
}