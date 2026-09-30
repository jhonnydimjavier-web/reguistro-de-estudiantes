package com.mycompany.reguistros.aplicacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mycompany.reguistros.dominio.excepcion.CursoNoEncontradoException;
import com.mycompany.reguistros.dominio.excepcion.CupoAgotadoException;
import com.mycompany.reguistros.dominio.excepcion.EstudianteNoEncontradoException;
import com.mycompany.reguistros.dominio.excepcion.LimiteCursosAlcanzadoException;
import com.mycompany.reguistros.dominio.excepcion.MatriculaDuplicadaException;
import com.mycompany.reguistros.dominio.excepcion.MatriculaNoEncontradaException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Casos limite: reglas de negocio y ausencia de efectos colaterales")
class CasosLimiteTest {

    private MatriculaService servicio;

    @BeforeEach
    void setUp() {
        servicio = Escenarios.nuevoServicioCon("Ana Ruiz", "Luis Paz", "Sara Gil");
    }

    @Test
    @DisplayName("Un curso con cupo 0 esta siempre agotado")
    void cursoSinCupoRechazaTodo() {
        servicio.registrarCurso("TALLER", "Taller cerrado", 0);

        assertThrows(CupoAgotadoException.class, () -> servicio.matricular("E001", "TALLER"));
        assertEquals(List.of(), servicio.listarEstudiantes("TALLER"));
    }

    @Test
    @DisplayName("El cupo se respeta exactamente: la ultima plaza disponible se ocupa")
    void respetaElCupoExacto() {
        servicio.registrarEstudiante("E004", "Marta Paz");
        servicio.registrarCurso("CAL", "Calculo", 3);

        servicio.matricular("E001", "CAL");
        servicio.matricular("E002", "CAL");
        servicio.matricular("E003", "CAL");
        assertEquals(0, servicio.buscarCurso("CAL").orElseThrow().plazasDisponibles(3));

        assertThrows(CupoAgotadoException.class, () -> servicio.matricular("E004", "CAL"));
    }

    @Test
    @DisplayName("Una matricula duplicada no crea una segunda relacion")
    void matriculaDuplicadaNoCambiaElEstado() {
        servicio.registrarCurso("BD", "Bases de Datos", 10);
        servicio.matricular("E001", "BD");

        assertThrows(MatriculaDuplicadaException.class, () -> servicio.matricular("E001", "BD"));
        assertThrows(MatriculaDuplicadaException.class, () -> servicio.matricular("E001", "bd"));

        assertEquals(1, servicio.listarEstudiantes("BD").size());
        assertEquals(List.of("BD"), servicio.listarCursos("E001").stream().map(c -> c.getCodigo()).toList());
    }

    @Test
    @DisplayName("Retirar una matricula inexistente no cambia el estado")
    void retiroInexistenteNoCambiaElEstado() {
        servicio.registrarCurso("BD", "Bases de Datos", 10);
        servicio.matricular("E001", "BD");

        assertThrows(MatriculaNoEncontradaException.class, () -> servicio.retirar("E002", "BD"));
        assertThrows(MatriculaNoEncontradaException.class, () -> servicio.retirar("E001", "RED"));
        assertThrows(MatriculaNoEncontradaException.class, () -> servicio.retirar("E999", "ZZZ"));
        assertThrows(MatriculaNoEncontradaException.class, () -> servicio.retirar("E001", "e001"));

        assertEquals(1, servicio.listarEstudiantes("BD").size());
        assertEquals(1, servicio.listarCursos("E001").size());
    }

    @Test
    @DisplayName("Retirar dos veces la misma matricula solo surte efecto la primera")
    void elSegundoRetiroNoSurteEfecto() {
        servicio.registrarCurso("BD", "Bases de Datos", 10);
        servicio.matricular("E001", "BD");
        servicio.matricular("E002", "BD");

        servicio.retirar("E001", "BD");
        assertThrows(MatriculaNoEncontradaException.class, () -> servicio.retirar("E001", "BD"));

        assertEquals(List.of("E002"),
                servicio.listarEstudiantes("BD").stream().map(e -> e.getCodigo()).toList());
    }

    @Test
    @DisplayName("Matricular exige que el estudiante y el curso existan")
    void matricularExigeEntidadesRegistradas() {
        servicio.registrarCurso("BD", "Bases de Datos", 10);

        assertThrows(EstudianteNoEncontradoException.class, () -> servicio.matricular("E404", "BD"));
        assertThrows(CursoNoEncontradoException.class, () -> servicio.matricular("E001", "FISICA"));

        assertEquals(List.of(), servicio.listarEstudiantes("BD"));
        assertEquals(List.of(), servicio.listarCursos("E001"));
    }

    @Test
    @DisplayName("Un estudiante no puede superar el maximo de cursos activos")
    void limiteDeCursosActivosPorEstudiante() {
        List<String> cursos = List.of("BD", "RED", "CAL", "BIO");
        cursos.forEach(codigo -> servicio.registrarCurso(codigo, "Curso " + codigo, 10));

        for (int i = 0; i < MatriculaService.MAX_CURSOS_ACTIVOS_POR_ESTUDIANTE; i++) {
            servicio.matricular("E001", cursos.get(i));
        }
        assertEquals(MatriculaService.MAX_CURSOS_ACTIVOS_POR_ESTUDIANTE, servicio.listarCursos("E001").size());

        assertThrows(LimiteCursosAlcanzadoException.class, () -> servicio.matricular("E001", cursos.get(3)));
        assertEquals(MatriculaService.MAX_CURSOS_ACTIVOS_POR_ESTUDIANTE, servicio.listarCursos("E001").size());
    }

    @Test
    @DisplayName("El limite de cursos activos es por estudiante, no global")
    void elLimiteEsPorEstudiante() {
        List<String> cursos = List.of("BD", "RED", "CAL");
        cursos.forEach(codigo -> servicio.registrarCurso(codigo, "Curso " + codigo, 10));

        cursos.forEach(codigo -> servicio.matricular("E001", codigo));
        cursos.forEach(codigo -> servicio.matricular("E002", codigo));

        assertEquals(3, servicio.listarCursos("E001").size());
        assertEquals(3, servicio.listarCursos("E002").size());
        assertEquals(2, servicio.listarEstudiantes("BD").size());
    }

    @Test
    @DisplayName("Retirar un curso vuelve a liberar un cupo de matricula del estudiante")
    void retirarLiberaCupoEnElLimiteDelEstudiante() {
        List<String> cursos = List.of("BD", "RED", "CAL", "BIO");
        cursos.forEach(codigo -> servicio.registrarCurso(codigo, "Curso " + codigo, 10));

        for (int i = 0; i < MatriculaService.MAX_CURSOS_ACTIVOS_POR_ESTUDIANTE; i++) {
            servicio.matricular("E001", cursos.get(i));
        }
        servicio.retirar("E001", cursos.get(0));

        servicio.matricular("E001", cursos.get(3));

        assertEquals(3, servicio.listarCursos("E001").size());
        assertTrue(servicio.listarCursos("E001").stream().map(c -> c.getCodigo()).toList().contains("BIO"));
    }

    @Test
    @DisplayName("Un rechazo por limite no impide matricular en otro curso libre del mismo estudiante")
    void elRechazoPorLimiteNoAfectaOtrosCursos() {
        List<String> cursos = List.of("BD", "RED", "CAL", "BIO");
        cursos.forEach(codigo -> servicio.registrarCurso(codigo, "Curso " + codigo, 10));
        for (int i = 0; i < MatriculaService.MAX_CURSOS_ACTIVOS_POR_ESTUDIANTE; i++) {
            servicio.matricular("E002", cursos.get(i));
        }

        assertThrows(LimiteCursosAlcanzadoException.class, () -> servicio.matricular("E002", cursos.get(3)));
        servicio.matricular("E001", cursos.get(3));

        assertEquals(1, servicio.listarCursos("E001").size());
        assertEquals(3, servicio.listarCursos("E002").size());
    }
}
