package com.example.demo.service;

import com.example.demo.entity.Solicitud;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;

@Service
public class GestorSolicitudes {

    private final ArrayList<Solicitud> solicitudes = new ArrayList<>();
    private final LinkedList<Solicitud> colaSolicitudes = new LinkedList<>();
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

    public int validarCargaInicial(List<Solicitud> solicitudesIniciales) {
        if (solicitudesIniciales == null) {
            throw new IllegalArgumentException(
                    "La lista de solicitudes iniciales no puede ser nula");
        }

        int descartadas = 0;
        for (int i = 0; i < solicitudesIniciales.size(); i++) {
            Solicitud solicitud = solicitudesIniciales.get(i);
            if (solicitud != null
                    && solicitud.getDescripcion() != null
                    && !solicitud.getDescripcion().isBlank()
                    && solicitud.getEstado() == Solicitud.Estado.PENDIENTE) {
                registrarSolicitud(solicitud);
            } else {
                descartadas++;
            }
        }
        return descartadas;
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

    public void restaurarSolicitudPendienteAlFrente(Solicitud solicitud) {
        solicitud.setEstado(Solicitud.Estado.PENDIENTE);
        colaSolicitudes.addFirst(solicitud);
        historialOperaciones.push(
                "Solicitud devuelta a la cola: " + solicitud.getDescripcion());
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

    public List<Solicitud> buscarPorDescripcion(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "El texto de búsqueda no puede estar vacío");
        }

        List<Solicitud> resultados = new ArrayList<>();
        String textoBuscado = texto.toLowerCase(Locale.ROOT);
        for (Solicitud solicitud : solicitudes) {
            String descripcion = solicitud.getDescripcion();
            if (descripcion != null
                    && descripcion.toLowerCase(Locale.ROOT).contains(textoBuscado)) {
                resultados.add(solicitud);
            }
        }
        return resultados;
    }

    public List<Solicitud> ordenarPorDescripcion() {
        List<Solicitud> ordenadas = new ArrayList<>(solicitudes);
        // Bubble sort compares adjacent descriptions and swaps them when out of order.
        for (int fin = ordenadas.size() - 1; fin > 0; fin--) {
            for (int i = 0; i < fin; i++) {
                String actual = descripcionParaOrdenar(ordenadas.get(i));
                String siguiente = descripcionParaOrdenar(ordenadas.get(i + 1));
                if (actual.compareToIgnoreCase(siguiente) > 0) {
                    Solicitud temporal = ordenadas.get(i);
                    ordenadas.set(i, ordenadas.get(i + 1));
                    ordenadas.set(i + 1, temporal);
                }
            }
        }
        return ordenadas;
    }

    private String descripcionParaOrdenar(Solicitud solicitud) {
        String descripcion = solicitud.getDescripcion();
        return descripcion == null ? "" : descripcion;
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
