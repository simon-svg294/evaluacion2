package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CLIENTE")
public class Cliente extends Usuario {

    private Integer puntosFidelidad = 0;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente")
    private List<Solicitud> solicitudes = new ArrayList<>();

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

    public List<Solicitud> getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(List<Solicitud> solicitudes) {
        this.solicitudes = solicitudes;
    }

    @Override
    public String obtenerDescripcion() {
        return "Cliente con " + puntosFidelidad
                + " puntos de fidelidad";
    }
}