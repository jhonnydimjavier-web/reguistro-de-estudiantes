package com.mycompany.reguistros.aplicacion;

import com.mycompany.reguistros.repositorio.memoria.CursoEnMemoriaRepository;
import com.mycompany.reguistros.repositorio.memoria.EstudianteEnMemoriaRepository;
import com.mycompany.reguistros.repositorio.memoria.MatriculaEnMemoriaRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public final class Escenarios {

    public static final LocalDate FECHA_FIJA = LocalDate.of(2026, 3, 15);

    private Escenarios() {
    }

    public static MatriculaService nuevoServicio() {
        Clock relojFijo = Clock.fixed(Instant.parse("2026-03-15T10:00:00Z"), ZoneOffset.UTC);
        return new MatriculaService(
                new EstudianteEnMemoriaRepository(),
                new CursoEnMemoriaRepository(),
                new MatriculaEnMemoriaRepository(),
                relojFijo);
    }

    public static MatriculaService nuevoServicioCon(String... nombres) {
        MatriculaService servicio = nuevoServicio();
        for (int i = 0; i < nombres.length; i++) {
            servicio.registrarEstudiante(codigoEstudiante(i), nombres[i]);
        }
        return servicio;
    }

    public static String codigoEstudiante(int indice) {
        return String.format("E%03d", indice + 1);
    }
}
