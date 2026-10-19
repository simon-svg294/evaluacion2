package com.example.demo.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@DiscriminatorValue("ADMINISTRADOR")
public class Administrador extends Usuario {

    @Min(1)
    @Max(5)
    private Integer nivelAcceso;

    public Administrador() {
    }

    public Administrador(String nombre, String correo,
                         Integer nivelAcceso) {
        super(nombre, correo);
        this.nivelAcceso = nivelAcceso;
    }

    public Integer getNivelAcceso() {
        return nivelAcceso;
    }

    public void setNivelAcceso(Integer nivelAcceso) {
        this.nivelAcceso = nivelAcceso;
    }

    @Override
    public String obtenerDescripcion() {
        return "Administrador con nivel de acceso " + nivelAcceso;
    }
}
