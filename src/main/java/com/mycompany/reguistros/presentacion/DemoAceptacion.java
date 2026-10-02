package com.mycompany.reguistros.presentacion;

import com.mycompany.reguistros.aplicacion.MatriculaService;
import com.mycompany.reguistros.dominio.Curso;
import com.mycompany.reguistros.dominio.excepcion.ReglaDeNegocioException;
import java.util.List;

public final class DemoAceptacion {

    public static final String PERIODO = "2026-I";

    private record EstudianteDemo(String codigo, String nombre) {
    }

    private record CursoDemo(String codigo, String nombre, int cupo) {
    }

    private record MatriculaDemo(String estudiante, String curso) {
    }

    private static final List<EstudianteDemo> ESTUDIANTES = List.of(
            new EstudianteDemo("E001", "Ana María Ruiz"),
            new EstudianteDemo("E002", "Luis Fernando Paz"),
            new EstudianteDemo("E003", "Sara Valentina Gil"),
            new EstudianteDemo("E004", "Mateo Alejandro Soto"),
            new EstudianteDemo("E005", "Camila Restrepo"),
            new EstudianteDemo("E006", "Joaquín Herrera"),
            new EstudianteDemo("E007", "Juliana Cárdenas"),
            new EstudianteDemo("E008", "Diego Montoya"));

    private static final List<CursoDemo> CURSOS = List.of(
            new CursoDemo("POO", "Programación Orientada a Objetos", 2),
            new CursoDemo("BD", "Bases de Datos Relacionales", 4),
            new CursoDemo("RED", "Redes de Computadores", 3),
            new CursoDemo("CAL", "Cálculo Diferencial", 6),
            new CursoDemo("BIO", "Biología Celular", 5),
            new CursoDemo("ALG", "Algoritmos y Estructuras de Datos", 0),
            new CursoDemo("ING", "Inglés Técnico", 8));

    private static final List<MatriculaDemo> MATRICULAS = List.of(
            new MatriculaDemo("E001", "POO"),
            new MatriculaDemo("E002", "POO"),
            new MatriculaDemo("E002", "RED"),
            new MatriculaDemo("E003", "RED"),
            new MatriculaDemo("E004", "RED"),
            new MatriculaDemo("E001", "BD"),
            new MatriculaDemo("E005", "BD"),
            new MatriculaDemo("E006", "BD"),
            new MatriculaDemo("E003", "CAL"),
            new MatriculaDemo("E004", "CAL"),
            new MatriculaDemo("E007", "CAL"),
            new MatriculaDemo("E005", "BIO"),
            new MatriculaDemo("E007", "BIO"),
            new MatriculaDemo("E007", "ING"),
            new MatriculaDemo("E008", "ING"));

    private DemoAceptacion() {
    }

    /**
     * Deja el sistema con datos que ejercitan todas las reglas: cursos al 100 % del cupo,
     * un curso cerrado con cupo 0, un estudiante en el máximo de cursos activos y un
     * estudiante todavía sin matrícula.
     */
    public static void cargar(MatriculaService servicio) {
        CURSOS.forEach(curso -> servicio.registrarCurso(curso.codigo(), curso.nombre(), curso.cupo()));
        ESTUDIANTES.forEach(estudiante -> servicio.registrarEstudiante(estudiante.codigo(), estudiante.nombre()));
        MATRICULAS.forEach(matricula -> servicio.matricular(matricula.estudiante(), matricula.curso()));
    }

    public static void mostrar(MatriculaService servicio) {
        System.out.print("""
                Escenario de aceptación: curso con cupo 2
                """);
        Curso poo = servicio.registrarCurso("POO", "Programación Orientada a Objetos", 2);
        servicio.registrarEstudiante("E001", "Ana María Ruiz");
        servicio.registrarEstudiante("E002", "Luis Fernando Paz");
        servicio.registrarEstudiante("E003", "Sara Valentina Gil");

        intentar("E001 se matricula en POO", () -> servicio.matricular("E001", "POO"));
        intentar("E002 se matricula en POO", () -> servicio.matricular("E002", "POO"));
        intentar("E003 se matricula en POO (tercero, sin cupo)", () -> servicio.matricular("E003", "POO"));
        intentar("E001 se matricula otra vez en POO (duplicada)", () -> servicio.matricular("E001", "POO"));
        System.out.println("  Estado del curso POO: %d de %d plazas"
                .formatted(servicio.listarEstudiantes("POO").size(), poo.getCupo()));

        System.out.println("""
                
                Retiro libera una plaza
                """);
        intentar("E002 retira su matrícula de POO", () -> servicio.retirar("E002", "POO"));
        intentar("E002 retira de nuevo (matrícula inexistente)", () -> servicio.retirar("E002", "POO"));
        intentar("E003 se matricula en POO", () -> servicio.matricular("E003", "POO"));

        System.out.println("""

                Máximo de %d cursos activos por estudiante
                """.formatted(MatriculaService.MAX_CURSOS_ACTIVOS_POR_ESTUDIANTE));
        servicio.registrarCurso("BD", "Bases de Datos", 10);
        servicio.registrarCurso("RED", "Redes", 10);
        servicio.registrarCurso("CAL", "Cálculo", 10);

        intentar("E001 se matricula en BD (2.º curso)", () -> servicio.matricular("E001", "BD"));
        intentar("E001 se matricula en RED (3.er curso)", () -> servicio.matricular("E001", "RED"));
        intentar("E001 se matricula en CAL (4.º curso)", () -> servicio.matricular("E001", "CAL"));
        intentar("E001 se matricula otra vez en POO (duplicada)", () -> servicio.matricular("E001", "POO"));
        System.out.println("  Cursos activos de E001: %d"
                .formatted(servicio.listarCursos("E001").size()));

        System.out.println("""

                Consultas
                """);
        System.out.println("  Cursos de E001: " + servicio.listarCursos("E001"));
        System.out.println("  Estudiantes de POO: " + servicio.listarEstudiantes("POO"));
        System.out.println("  Cursos registrados: " + servicio.listarCursos().size());
        System.out.println("  Estudiantes registrados: " + servicio.listarEstudiantes().size());
    }

    private static void intentar(String descripcion, Runnable operacion) {
        try {
            operacion.run();
            System.out.println("  [OK]        " + descripcion);
        } catch (ReglaDeNegocioException excepcion) {
            System.out.println("  [RECHAZADO] %s -> %s".formatted(descripcion, excepcion.getMessage()));
        }
    }
}