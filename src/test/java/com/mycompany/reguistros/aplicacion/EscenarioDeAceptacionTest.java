package com.mycompany.reguistros.aplicacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mycompany.reguistros.dominio.Curso;
import com.mycompany.reguistros.dominio.Estudiante;
import com.mycompany.reguistros.dominio.Matricula;
import com.mycompany.reguistros.dominio.excepcion.CupoAgotadoException;
import com.mycompany.reguistros.dominio.excepcion.MatriculaDuplicadaException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Escenario de aceptacion: cupo 2, rechazo del tercero y del duplicado, retiro libera plaza")
class EscenarioDeAceptacionTest {

    private final MatriculaService servicio = Escenarios.nuevoServicioCon("Ana Ruiz", "Luis Paz", "Sara Gil");
    private final Curso poo = servicio.registrarCurso("POO", "Programacion Orientada a Objetos", 2);

    @Test
    @DisplayName("Los dos primeros estudiantes ocupan el curso y el tercero es rechazado")
    void ocupaElCursoYRechazaAlTercero() {
        servicio.matricular("E001", "POO");
        servicio.matricular("E002", "POO");

        assertEquals(2, servicio.listarEstudiantes("POO").size());
        assertEquals(0, poo.plazasDisponibles(2));

        assertThrows(CupoAgotadoException.class, () -> servicio.matricular("E003", "POO"));
        assertEquals(2, servicio.listarEstudiantes("POO").size(), "el rechazo no debe alterar el estado");
    }

    @Test
    @DisplayName("Una matricula repetida es rechazada aunque queden plazas libres")
    void rechazaMatriculaDuplicada() {
        poo.setCupo(5);
        servicio.matricular("E001", "POO");

        assertThrows(MatriculaDuplicadaException.class, () -> servicio.matricular("E001", "POO"));
        assertEquals(1, servicio.listarEstudiantes("POO").size());
    }

    @Test
    @DisplayName("Retirar una matricula libera la plaza para el estudiante que quedo fuera")
    void elRetiroLiberaLaPlaza() {
        servicio.matricular("E001", "POO");
        servicio.matricular("E002", "POO");
        assertThrows(CupoAgotadoException.class, () -> servicio.matricular("E003", "POO"));

        Matricula retirada = servicio.retirar("E001", "POO");

        assertEquals(new Estudiante("E001", "Ana Ruiz").getCodigo(), retirada.getEstudiante().getCodigo());
        assertEquals(1, poo.plazasDisponibles(1));
        servicio.matricular("E003", "POO");

        assertEquals(List.of("E002", "E003"),
                servicio.listarEstudiantes("POO").stream().map(Estudiante::getCodigo).toList());
        assertEquals(poo.getCupo(), servicio.listarEstudiantes("POO").size());
    }

    @Test
    @DisplayName("La matricula queda registrada con la fecha del periodo academico en curso")
    void registraLaFechaDeLaMatricula() {
        Matricula matricula = servicio.matricular("E001", "POO");

        assertEquals(Escenarios.FECHA_FIJA, matricula.getFecha());
        assertEquals("E001|POO", matricula.getId().toString());
    }

    @Test
    @DisplayName("El retiro devuelve la matricula eliminada y ya no figura en el curso")
    void elRetiroDevuelveLaMatriculaEliminada() {
        servicio.matricular("E001", "POO");
        servicio.matricular("E002", "POO");

        Matricula retirada = servicio.retirar("E001", "POO");

        assertEquals("E001|POO", retirada.getId().toString());
        assertEquals(LocalDate.class, retirada.getFecha().getClass());
        assertEquals(List.of("E002"),
                servicio.listarEstudiantes("POO").stream().map(Estudiante::getCodigo).toList());
        assertEquals(List.of(poo), servicio.listarCursos("E002"));
    }
}
