package com.example.demo.service;

import com.example.demo.entity.Solicitud;
import com.example.demo.repository.SolicitudRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class CargaSolicitudesPendientes implements ApplicationRunner {

    private final SolicitudRepository solicitudRepository;
    private final GestorSolicitudes gestorSolicitudes;

    public CargaSolicitudesPendientes(SolicitudRepository solicitudRepository,
                                      GestorSolicitudes gestorSolicitudes) {
        this.solicitudRepository = solicitudRepository;
        this.gestorSolicitudes = gestorSolicitudes;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (Solicitud solicitud : solicitudRepository.findByEstado(
                Solicitud.Estado.PENDIENTE)) {
            gestorSolicitudes.registrarSolicitud(solicitud);
        }
    }
}
