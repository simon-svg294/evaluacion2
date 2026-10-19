package com.example.demo.service;

import com.example.demo.entity.Solicitud;
import com.example.demo.repository.SolicitudRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CargaSolicitudesPendientes implements ApplicationRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(CargaSolicitudesPendientes.class);

    private final SolicitudRepository solicitudRepository;
    private final GestorSolicitudes gestorSolicitudes;

    public CargaSolicitudesPendientes(SolicitudRepository solicitudRepository,
                                      GestorSolicitudes gestorSolicitudes) {
        this.solicitudRepository = solicitudRepository;
        this.gestorSolicitudes = gestorSolicitudes;
    }

    @Override
    public void run(ApplicationArguments args) {
        int descartadas = gestorSolicitudes.validarCargaInicial(
                solicitudRepository.findByEstado(Solicitud.Estado.PENDIENTE));
        if (descartadas > 0) {
            logger.warn("Se descartaron {} solicitudes inválidas en la carga inicial",
                    descartadas);
        }
    }
}
