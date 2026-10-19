package com.example.demo.controller;

import com.example.demo.entity.Solicitud;
import com.example.demo.entity.Cliente;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.SolicitudRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.GestorSolicitudes;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    public SolicitudController(GestorSolicitudes gestorSolicitudes,
                               SolicitudRepository solicitudRepository,
                               UsuarioRepository usuarioRepository) {
        this.gestorSolicitudes = gestorSolicitudes;
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public Page<Solicitud> listar(
            @PageableDefault(size = 10, sort = "descripcion",
                    direction = Sort.Direction.ASC) Pageable pageable) {
        return solicitudRepository.findAll(pageable);
    }

    @GetMapping("/buscar")
    public List<Solicitud> buscarPorDescripcion(@RequestParam String texto) {
        try {
            return gestorSolicitudes.buscarPorDescripcion(texto);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }

    @GetMapping("/ordenadas")
    public List<Solicitud> ordenarPorDescripcion() {
        return gestorSolicitudes.ordenarPorDescripcion();
    }

    @GetMapping("/{id}")
    public Solicitud buscarPorId(@PathVariable Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una solicitud con ese id"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Solicitud registrar(@Valid @RequestBody SolicitudRequest request) {
        Usuario usuario = usuarioRepository.findById(request.clienteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un cliente con ese id"));
        if (!(usuario instanceof Cliente cliente)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No existe un cliente con ese id");
        }

        Solicitud solicitud = new Solicitud(request.descripcion());
        solicitud.setCliente(cliente);
        Solicitud guardada = solicitudRepository.save(solicitud);
        gestorSolicitudes.registrarSolicitud(guardada);
        return guardada;
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
        Solicitud solicitud = gestorSolicitudes.procesarSiguienteSolicitud()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No hay solicitudes pendientes"));
        try {
            return solicitudRepository.save(solicitud);
        } catch (RuntimeException exception) {
            gestorSolicitudes.restaurarSolicitudPendienteAlFrente(solicitud);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo guardar el estado de la solicitud; se devolvió a la cola",
                    exception);
        }
    }

    @PostMapping("/procesar-todas")
    public Map<String, Integer> procesarTodas() {
        int procesadas = gestorSolicitudes.procesarTodasLasSolicitudes();
        solicitudRepository.saveAll(gestorSolicitudes.getSolicitudes());
        return Map.of("procesadas", procesadas);
    }

    @GetMapping("/historial")
    public List<String> consultarHistorial() {
        List<String> historial =
                new ArrayList<>(gestorSolicitudes.getHistorialOperaciones());
        Collections.reverse(historial);
        return historial;
    }

    public record SolicitudRequest(
            @NotBlank String descripcion,
            @NotNull Long clienteId) {
    }
}
