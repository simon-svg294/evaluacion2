package com.example.demo.service;

import com.example.demo.entity.Solicitud;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GestorSolicitudesTest {

    @Test
    void procesaSolicitudesEnOrdenDeLlegadaYActualizaElHistorial() {
        GestorSolicitudes gestor = new GestorSolicitudes();
        Solicitud primera = new Solicitud("Actualizar datos");
        Solicitud segunda = new Solicitud("Solicitar soporte");

        gestor.registrarSolicitud(primera);
        gestor.registrarSolicitud(segunda);

        assertSame(primera, gestor.procesarSiguienteSolicitud().orElseThrow());
        assertEquals(Solicitud.Estado.PROCESADA, primera.getEstado());
        assertSame(segunda, gestor.obtenerSolicitudMasReciente().orElseThrow());
        assertEquals(1, gestor.contarSolicitudesPorEstado(Solicitud.Estado.PENDIENTE));
        assertEquals(1, gestor.contarSolicitudesPorEstado(Solicitud.Estado.PROCESADA));
        assertEquals(1, gestor.procesarTodasLasSolicitudes());
        assertEquals(0, gestor.contarSolicitudesPorEstado(Solicitud.Estado.PENDIENTE));
        assertEquals(4, gestor.getHistorialOperaciones().size());
    }

    @Test
    void rechazaSolicitudesInvalidas() {
        GestorSolicitudes gestor = new GestorSolicitudes();

        assertThrows(IllegalArgumentException.class,
                () -> gestor.registrarSolicitud(null));
        assertThrows(IllegalArgumentException.class,
                () -> gestor.registrarSolicitud(new Solicitud("  ")));

        Solicitud procesada = new Solicitud("Solicitud ya procesada");
        procesada.setEstado(Solicitud.Estado.PROCESADA);
        assertThrows(IllegalArgumentException.class,
                () -> gestor.registrarSolicitud(procesada));
        assertEquals(0, gestor.getSolicitudes().size());
    }
}
