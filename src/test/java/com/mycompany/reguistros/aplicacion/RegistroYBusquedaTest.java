package com.mycompany.reguistros.aplicacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mycompany.reguistros.dominio.Curso;
import com.mycompany.reguistros.dominio.Estudiante;
import com.mycompany.reguistros.dominio.excepcion.CursoYaRegistradoException;
import com.mycompany.reguistros.dominio.excepcion.DatosInvalidosException;
import com.mycompany.reguistros.dominio.excepcion.EstudianteNoEncontradoException;
import com.mycompany.reguistros.dominio.excepcion.EstudianteYaRegistradoException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Registro y busqueda de estudiantes y cursos")
class RegistroYBusquedaTest {

    private MatriculaService servicio;

    @BeforeEach
    void setUp() {
        servicio = Escenarios.nuevoServicio();
    }

    @Test
    @DisplayName("Registra estudiantes y cursos y los devuelve con sus datos")
    void registraEstudiantesYCursos() {
        Estudiante estudiante = servicio.registrarEstudiante("E001", "Ana Ruiz");
        Curso curso = servicio.registrarCurso("POO", "Programacion Orientada a Objetos", 30);

        assertEquals("E001", estudiante.getCodigo());
        assertEquals("Ana Ruiz", estudiante.getNombre());
        assertEquals("POO", curso.getCodigo());
        assertEquals(30, curso.getCupo());
        assertEquals(30, curso.plazasDisponibles(0));
    }

    @Test
    @DisplayName("El codigo se normaliza a mayusculas y sin espacios")
    void normalizaLosCodigos() {
        Estudiante estudiante = servicio.registrarEstudiante("  e001 ", "  Ana Ruiz  ");
        servicio.registrarCurso(" poo ", "Programacion", 10);

        assertEquals("E001", estudiante.getCodigo());
        assertEquals("Ana Ruiz", estudiante.getNombre());
        assertTrue(servicio.buscarCurso("POO").isPresent());
    }

    @Test
    @DisplayName("Busca estudiantes y cursos por codigo, tambien en minuscula")
    void buscaPorCodigo() {
        servicio.registrarEstudiante("E001", "Ana Ruiz");
        servicio.registrarCurso("POO", "Programacion", 10);

        assertEquals(Optional.of("Ana Ruiz"),
                servicio.buscarEstudiante("e001").map(Estudiante::getNombre));
        assertEquals(Optional.of("POO"),
                servicio.buscarCurso("poo").map(Curso::getCodigo));
        assertTrue(servicio.buscarEstudiante("E999").isEmpty());
        assertTrue(servicio.buscarCurso("FISICA").isEmpty());
    }

    @Test
    @DisplayName("No admite dos estudiantes ni dos cursos con el mismo codigo")
    void rechazaCodigosRepetidos() {
        servicio.registrarEstudiante("E001", "Ana Ruiz");
        servicio.registrarCurso("POO", "Programacion", 10);

        assertThrows(EstudianteYaRegistradoException.class, () -> servicio.registrarEstudiante("e001", "Otro"));
        assertThrows(CursoYaRegistradoException.class, () -> servicio.registrarCurso(" POO ", "Otro", 5));

        assertEquals(1, servicio.listarEstudiantes().size());
        assertEquals(1, servicio.listarCursos().size());
        assertEquals("Ana Ruiz", servicio.buscarEstudiante("E001").orElseThrow().getNombre());
    }

    @Test
    @DisplayName("Rechaza datos obligatorios vacios")
    void rechazaDatosInvalidos() {
        assertThrows(DatosInvalidosException.class, () -> servicio.registrarEstudiante(null, "Ana"));
        assertThrows(DatosInvalidosException.class, () -> servicio.registrarEstudiante("E001", "   "));
        assertThrows(DatosInvalidosException.class, () -> servicio.registrarCurso("POO", null, 10));
        assertThrows(DatosInvalidosException.class, () -> servicio.registrarCurso("POO", "Programacion", -1));
        assertThrows(EstudianteNoEncontradoException.class, () -> servicio.listarCursos("E404"));
    }

    @Test
    @DisplayName("Lista todos los estudiantes y cursos registrados en orden de registro")
    void listaLosRegistrosEnOrdenDeIngreso() {
        servicio.registrarEstudiante("E001", "Ana Ruiz");
        servicio.registrarEstudiante("E002", "Luis Paz");
        servicio.registrarCurso("POO", "Programacion", 10);
        servicio.registrarCurso("BD", "Bases de Datos", 10);

        assertEquals(List.of("E001", "E002"),
                servicio.listarEstudiantes().stream().map(Estudiante::getCodigo).toList());
        assertEquals(List.of("POO", "BD"),
                servicio.listarCursos().stream().map(Curso::getCodigo).toList());
    }

    @Test
    @DisplayName("Consulta los cursos de un estudiante y los estudiantes de un curso")
    void consultaLasRelaciones() {
        servicio.registrarEstudiante("E001", "Ana Ruiz");
        servicio.registrarEstudiante("E002", "Luis Paz");
        servicio.registrarCurso("POO", "Programacion", 10);
        servicio.registrarCurso("BD", "Bases de Datos", 10);
        servicio.registrarCurso("FISICA", "Fisica", 10);
        servicio.matricular("E001", "POO");
        servicio.matricular("E001", "BD");
        servicio.matricular("E002", "BD");

        assertEquals(List.of("POO", "BD"),
                servicio.listarCursos("E001").stream().map(Curso::getCodigo).toList());
        assertEquals(List.of("E001", "E002"),
                servicio.listarEstudiantes("BD").stream().map(Estudiante::getCodigo).toList());
        assertEquals(List.of("E001|POO", "E001|BD", "E002|BD"),
                servicio.listarMatriculas().stream().map(matricula -> matricula.getId().toString()).toList());
        assertEquals(List.of(), servicio.listarEstudiantes("FISICA"));
        assertThrows(EstudianteNoEncontradoException.class, () -> servicio.listarCursos("E404"));
    }
}
