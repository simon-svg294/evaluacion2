package com.example.demo.service;

import com.example.demo.entity.Solicitud;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

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

    @Test
    void cargaSoloSolicitudesValidasYCuentaLasDescartadas() {
        GestorSolicitudes gestor = new GestorSolicitudes();
        Solicitud valida = new Solicitud("Solicitud pendiente");
        Solicitud sinDescripcion = new Solicitud(" ");
        Solicitud procesada = new Solicitud("Solicitud ya procesada");
        procesada.setEstado(Solicitud.Estado.PROCESADA);

        int descartadas = gestor.validarCargaInicial(
                Arrays.asList(valida, sinDescripcion, null, procesada));

        assertEquals(3, descartadas);
        assertEquals(List.of(valida), gestor.getSolicitudes());
        assertEquals(List.of(valida), List.copyOf(gestor.getColaSolicitudes()));
        assertEquals(List.of(valida), gestor.getPilaSolicitudes());
    }

    @Test
    void buscaPorDescripcionSinDistinguirMayusculas() {
        GestorSolicitudes gestor = new GestorSolicitudes();
        Solicitud encontrada = new Solicitud("Solicitar SOPORTE");
        gestor.registrarSolicitud(encontrada);
        gestor.registrarSolicitud(new Solicitud("Actualizar datos"));

        assertEquals(List.of(encontrada), gestor.buscarPorDescripcion("soporte"));
        assertThrows(IllegalArgumentException.class,
                () -> gestor.buscarPorDescripcion(" "));
    }

    @Test
    void ordenaPorDescripcionSinCambiarLaListaOriginal() {
        GestorSolicitudes gestor = new GestorSolicitudes();
        Solicitud zeta = new Solicitud("Zeta");
        Solicitud alfa = new Solicitud("Alfa");
        gestor.registrarSolicitud(zeta);
        gestor.registrarSolicitud(alfa);

        assertEquals(List.of(alfa, zeta), gestor.ordenarPorDescripcion());
        assertEquals(List.of(zeta, alfa), gestor.getSolicitudes());
    }

    @Test
    void ordenarPorDescripcionDevuelveListaVaciaCuandoNoHaySolicitudes() {
        GestorSolicitudes gestor = new GestorSolicitudes();

        assertEquals(List.of(), gestor.ordenarPorDescripcion());
    }

    @Test
    void ordenarPorDescripcionNoModificaElOrdenDeLaCola() {
        GestorSolicitudes gestor = new GestorSolicitudes();
        Solicitud zeta = new Solicitud("Zeta");
        Solicitud alfa = new Solicitud("Alfa");
        gestor.registrarSolicitud(zeta);
        gestor.registrarSolicitud(alfa);

        gestor.ordenarPorDescripcion();

        assertEquals(List.of(zeta, alfa), List.copyOf(gestor.getColaSolicitudes()));
        assertEquals(List.of(zeta, alfa), gestor.getPilaSolicitudes());
    }

    @Test
    void ordenarPorDescripcionTrataLaDescripcionNulaComoTextoVacio() {
        GestorSolicitudes gestor = new GestorSolicitudes();
        Solicitud sinDescripcion = new Solicitud("Temporal");
        Solicitud conDescripcion = new Solicitud("Solicitud");
        gestor.registrarSolicitud(sinDescripcion);
        gestor.registrarSolicitud(conDescripcion);
        sinDescripcion.setDescripcion(null);

        assertEquals(List.of(sinDescripcion, conDescripcion),
                gestor.ordenarPorDescripcion());
    }

    @Test
    void restaurarSolicitudLaDevuelvePendienteAlFrenteDeLaCola() {
        GestorSolicitudes gestor = new GestorSolicitudes();
        Solicitud primera = new Solicitud("Primera solicitud");
        Solicitud segunda = new Solicitud("Segunda solicitud");
        gestor.registrarSolicitud(primera);
        gestor.registrarSolicitud(segunda);

        assertSame(primera, gestor.procesarSiguienteSolicitud().orElseThrow());
        gestor.restaurarSolicitudPendienteAlFrente(primera);

        assertEquals(Solicitud.Estado.PENDIENTE, primera.getEstado());
        assertEquals(List.of(primera, segunda),
                List.copyOf(gestor.getColaSolicitudes()));
    }
}
