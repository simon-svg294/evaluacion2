package com.example.demo.controller;

import com.example.demo.entity.Solicitud;
import com.example.demo.service.GestorSolicitudes;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final GestorSolicitudes gestorSolicitudes;

    public SolicitudController(GestorSolicitudes gestorSolicitudes) {
        this.gestorSolicitudes = gestorSolicitudes;
    }

    @GetMapping
    public List<Solicitud> listar() {
        return gestorSolicitudes.getSolicitudes();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Solicitud registrar(@Valid @RequestBody SolicitudRequest request) {
        Solicitud solicitud = new Solicitud(request.descripcion());
        gestorSolicitudes.registrarSolicitud(solicitud);
        return solicitud;
    }

    @GetMapping("/cola")
    public List<Solicitud> consultarCola() {
        return new ArrayList<>(gestorSolicitudes.getColaSolicitudes());
    }

    @GetMapping("/reciente")
    public Solicitud consultarMasReciente() {
        return gestorSolicitudes.obtenerSolicitudMasReciente()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No hay solicitudes registradas"));
    }

    @PostMapping("/procesar")
    public Solicitud procesarSiguiente() {
        return gestorSolicitudes.procesarSiguienteSolicitud()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NO_CONTENT, "No hay solicitudes pendientes"));
    }

    @PostMapping("/procesar-todas")
    public Map<String, Integer> procesarTodas() {
        int procesadas = gestorSolicitudes.procesarTodasLasSolicitudes();
        return Map.of("procesadas", procesadas);
    }

    @GetMapping("/historial")
    public List<String> consultarHistorial() {
        List<String> historial =
                new ArrayList<>(gestorSolicitudes.getHistorialOperaciones());
        Collections.reverse(historial);
        return historial;
    }

    public record SolicitudRequest(@NotBlank String descripcion) {
    }
}
