package com.mycompany.reguistros.presentacion;

import com.mycompany.reguistros.aplicacion.MatriculaService;
import com.mycompany.reguistros.dominio.Curso;
import com.mycompany.reguistros.dominio.excepcion.ReglaDeNegocioException;

public final class DemoAceptacion {

    private DemoAceptacion() {
    }

    public static void cargar(MatriculaService servicio) {
        servicio.registrarCurso("POO", "Programacion Orientada a Objetos", 2);
        servicio.registrarEstudiante("E001", "Ana Ruiz");
        servicio.registrarEstudiante("E002", "Luis Paz");
        servicio.registrarEstudiante("E003", "Sara Gil");
        servicio.matricular("E001", "POO");
        servicio.matricular("E003", "POO");
        servicio.registrarCurso("BD", "Bases de Datos", 10);
        servicio.registrarCurso("RED", "Redes", 10);
        servicio.registrarCurso("CAL", "Calculo", 10);
        servicio.matricular("E001", "BD");
        servicio.matricular("E001", "RED");
    }

    public static void mostrar(MatriculaService servicio) {
        System.out.println("Escenario de aceptacion: curso con cupo 2");
        Curso poo = servicio.registrarCurso("POO", "Programacion Orientada a Objetos", 2);
        servicio.registrarEstudiante("E001", "Ana Ruiz");
        servicio.registrarEstudiante("E002", "Luis Paz");
        servicio.registrarEstudiante("E003", "Sara Gil");

        intentar("E001 se matricula en POO", () -> servicio.matricular("E001", "POO"));
        intentar("E002 se matricula en POO", () -> servicio.matricular("E002", "POO"));
        intentar("E003 se matricula en POO (tercero, sin cupo)", () -> servicio.matricular("E003", "POO"));
        intentar("E001 se matricula otra vez en POO (duplicada)", () -> servicio.matricular("E001", "POO"));
        System.out.println("  Estado del curso POO: " + servicio.listarEstudiantes("POO").size()
                + " de " + poo.getCupo() + " plazas");

        System.out.println();
        System.out.println("Retiro libera una plaza");
        intentar("E002 retira su matricula de POO", () -> servicio.retirar("E002", "POO"));
        intentar("E002 retira de nuevo (matricula inexistente)", () -> servicio.retirar("E002", "POO"));
        intentar("E003 se matricula en POO", () -> servicio.matricular("E003", "POO"));

        System.out.println();
        System.out.println("Maximo " + MatriculaService.MAX_CURSOS_ACTIVOS_POR_ESTUDIANTE
                + " cursos activos por estudiante");
        servicio.registrarCurso("BD", "Bases de Datos", 10);
        servicio.registrarCurso("RED", "Redes", 10);
        servicio.registrarCurso("CAL", "Calculo", 10);

        intentar("E001 se matricula en BD (2do curso)", () -> servicio.matricular("E001", "BD"));
        intentar("E001 se matricula en RED (3er curso)", () -> servicio.matricular("E001", "RED"));
        intentar("E001 se matricula en CAL (4to curso)", () -> servicio.matricular("E001", "CAL"));
        intentar("E001 se matricula otra vez en POO (duplicada)", () -> servicio.matricular("E001", "POO"));
        System.out.println("  Cursos activos de E001: " + servicio.listarCursos("E001").size());

        System.out.println();
        System.out.println("Consultas");
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
            System.out.println("  [RECHAZADO] " + descripcion + " -> " + excepcion.getMessage());
        }
    }
}