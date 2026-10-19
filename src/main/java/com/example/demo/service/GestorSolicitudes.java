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
        } else if (solicitud.getDescripcion() == null
                || solicitud.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        } else if (solicitud.getEstado() != Solicitud.Estado.PENDIENTE) {
            throw new IllegalArgumentException(
                    "Solo se pueden registrar solicitudes pendientes");
        } else {
            solicitudes.add(solicitud);
            colaSolicitudes.offer(solicitud);
            pilaSolicitudes.push(solicitud);
            historialOperaciones.push(
                    "Solicitud registrada: " + solicitud.getDescripcion());
        }
    }

    public Optional<Solicitud> procesarSiguienteSolicitud() {
        Solicitud solicitud = colaSolicitudes.poll();
        if (solicitud == null) {
            return Optional.empty();
        }

        switch (solicitud.getEstado()) {
            case PENDIENTE -> {
                solicitud.setEstado(Solicitud.Estado.PROCESADA);
                historialOperaciones.push(
                        "Solicitud procesada: " + solicitud.getDescripcion());
            }
            case PROCESADA -> historialOperaciones.push(
                    "Solicitud omitida porque ya estaba procesada: "
                            + solicitud.getDescripcion());
            case RECHAZADA -> historialOperaciones.push(
                    "Solicitud omitida porque fue rechazada: "
                            + solicitud.getDescripcion());
        }
        return Optional.of(solicitud);
    }

    public int procesarTodasLasSolicitudes() {
        int procesadas = 0;
        while (!colaSolicitudes.isEmpty()) {
            if (colaSolicitudes.peek().getEstado() == Solicitud.Estado.PENDIENTE) {
                procesadas++;
            }
            procesarSiguienteSolicitud();
        }
        return procesadas;
    }

    public int contarSolicitudesPorEstado(Solicitud.Estado estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }

        int cantidad = 0;
        for (Solicitud solicitud : solicitudes) {
            switch (solicitud.getEstado()) {
                case PENDIENTE -> {
                    if (estado == Solicitud.Estado.PENDIENTE) {
                        cantidad++;
                    }
                }
                case PROCESADA -> {
                    if (estado == Solicitud.Estado.PROCESADA) {
                        cantidad++;
                    }
                }
                case RECHAZADA -> {
                    if (estado == Solicitud.Estado.RECHAZADA) {
                        cantidad++;
                    }
                }
            }
        }
        return cantidad;
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
