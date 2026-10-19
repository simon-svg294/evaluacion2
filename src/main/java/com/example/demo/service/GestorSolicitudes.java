package com.example.demo.service;

import com.example.demo.entity.Solicitud;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;

public class GestorSolicitudes {

    private final ArrayList<Solicitud> solicitudes = new ArrayList<>();
    private final Queue<Solicitud> colaSolicitudes = new LinkedList<>();
    private final Stack<Solicitud> pilaSolicitudes = new Stack<>();
    private final Stack<String> historialOperaciones = new Stack<>();

    public void registrarSolicitud(Solicitud solicitud) {
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }

        solicitudes.add(solicitud);
        colaSolicitudes.offer(solicitud);
        pilaSolicitudes.push(solicitud);
        historialOperaciones.push("Solicitud registrada");
    }

    public Optional<Solicitud> procesarSiguienteSolicitud() {
        Solicitud solicitud = colaSolicitudes.poll();
        if (solicitud == null) {
            return Optional.empty();
        }

        historialOperaciones.push("Solicitud procesada");
        return Optional.of(solicitud);
    }

    public Optional<Solicitud> obtenerSolicitudMasReciente() {
        return pilaSolicitudes.empty()
                ? Optional.empty()
                : Optional.of(pilaSolicitudes.peek());
    }

    public ArrayList<Solicitud> getSolicitudes() {
        return new ArrayList<>(solicitudes);
    }

    public Queue<Solicitud> getColaSolicitudes() {
        return new LinkedList<>(colaSolicitudes);
    }

    public Stack<Solicitud> getPilaSolicitudes() {
        Stack<Solicitud> copia = new Stack<>();
        copia.addAll(pilaSolicitudes);
        return copia;
    }

    public Stack<String> getHistorialOperaciones() {
        Stack<String> copia = new Stack<>();
        copia.addAll(historialOperaciones);
        return copia;
    }
}
