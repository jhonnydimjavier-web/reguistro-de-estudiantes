package com.mycompany.reguistros.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mycompany.reguistros.dominio.excepcion.DatosInvalidosException;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Invariantes del dominio: estudiantes, cursos y matriculas")
class DominioTest {

    @Test
    @DisplayName("El estudiante exige codigo y nombre")
    void estudianteExigeCodigoYNombre() {
        assertThrows(DatosInvalidosException.class, () -> new Estudiante(null, "Ana"));
        assertThrows(DatosInvalidosException.class, () -> new Estudiante("  ", "Ana"));
        assertThrows(DatosInvalidosException.class, () -> new Estudiante("E001", null));
        assertThrows(DatosInvalidosException.class, () -> new Estudiante("E001", "  "));
    }

    @Test
    @DisplayName("Dos estudiantes con el mismo codigo son la misma entidad")
    void laIdentidadEsElCodigo() {
        Estudiante uno = new Estudiante("E001", "Ana Ruiz");
        Estudiante otro = new Estudiante("e001", "Nombre distinto");

        assertEquals(uno, otro);
        assertEquals(uno.hashCode(), otro.hashCode());
        assertNotEquals(uno, new Estudiante("E002", "Ana Ruiz"));
    }

    @Test
    @DisplayName("El curso exige un cupo no negativo")
    void cursoExigeCupoNoNegativo() {
        assertThrows(DatosInvalidosException.class, () -> new Curso("POO", "Programacion", -1));
        assertThrows(DatosInvalidosException.class, () -> new Curso("POO", "Programacion", 0).setCupo(-5));
        assertEquals(0, new Curso("CERRADO", "Curso cerrado", 0).getCupo());
    }

    @Test
    @DisplayName("El curso calcula las plazas disponibles")
    void calculaPlazasDisponibles() {
        Curso curso = new Curso("POO", "Programacion", 3);

        assertTrue(curso.tieneCupoDisponible(2));
        assertFalse(curso.tieneCupoDisponible(3));
        assertFalse(curso.tieneCupoDisponible(4));
        assertEquals(1, curso.plazasDisponibles(2));
        assertEquals(0, curso.plazasDisponibles(5));
    }

    @Test
    @DisplayName("La matricula exige estudiante, curso y fecha")
    void matriculaExcedeSusPartes() {
        Estudiante estudiante = new Estudiante("E001", "Ana Ruiz");
        Curso curso = new Curso("POO", "Programacion", 10);

        assertThrows(DatosInvalidosException.class, () -> new Matricula(null, curso, LocalDate.now()));
        assertThrows(DatosInvalidosException.class, () -> new Matricula(estudiante, null, LocalDate.now()));
        assertThrows(DatosInvalidosException.class, () -> new Matricula(estudiante, curso, null));
    }

    @Test
    @DisplayName("La matricula se identifica por la pareja estudiante-curso")
    void laMatriculaTieneIdentidadCompuesta() {
        Estudiante estudiante = new Estudiante("E001", "Ana Ruiz");
        Curso poo = new Curso("POO", "Programacion", 10);
        Curso bd = new Curso("BD", "Bases de Datos", 10);
        LocalDate hoy = LocalDate.of(2026, 3, 15);

        Matricula primera = new Matricula(estudiante, poo, hoy);
        Matricula misma = new Matricula(new Estudiante("e001", "Ana"), new Curso("poo", "POO", 10), hoy);
        Matricula otra = new Matricula(estudiante, bd, hoy);

        assertEquals(new MatriculaId("E001", "POO"), primera.getId());
        assertEquals(primera, misma);
        assertEquals(primera.hashCode(), misma.hashCode());
        assertNotEquals(primera, otra);
    }

    @Test
    @DisplayName("La matricula expone el estudiante, el curso y la fecha registrados")
    void laMatriculaExponeSusPartes() {
        Estudiante estudiante = new Estudiante("E001", "Ana Ruiz");
        Curso curso = new Curso("POO", "Programacion", 10);
        LocalDate fecha = LocalDate.of(2026, 3, 15);

        Matricula matricula = new Matricula(estudiante, curso, fecha);

        assertEquals(estudiante, matricula.getEstudiante());
        assertEquals(curso, matricula.getCurso());
        assertEquals(fecha, matricula.getFecha());
    }
}
