package com.mycompany.reguistros.presentacion;

import com.mycompany.reguistros.aplicacion.MatriculaService;
import com.mycompany.reguistros.dominio.Curso;
import com.mycompany.reguistros.dominio.Estudiante;
import com.mycompany.reguistros.dominio.excepcion.ReglaDeNegocioException;
import java.util.List;
import java.util.Scanner;

public class ConsolaMatriculas {

    private final MatriculaService servicio;
    private final Scanner entrada;

    public ConsolaMatriculas(MatriculaService servicio) {
        this.servicio = servicio;
        this.entrada = new Scanner(System.in);
    }

    public void iniciar() {
        boolean activo = true;
        while (activo) {
            mostrarMenu();
            String opcion = pedir("Opción").trim();
            switch (opcion) {
                case "1" -> registrarEstudiante();
                case "2" -> registrarCurso();
                case "3" -> buscarEstudiante();
                case "4" -> buscarCurso();
                case "5" -> matricular();
                case "6" -> retirar();
                case "7" -> listarCursosDeEstudiante();
                case "8" -> listarEstudiantesDeCurso();
                case "9" -> listarEstudiantes();
                case "10" -> listarCursos();
                case "0" -> activo = false;
                default -> System.out.println("Opción no válida.");
            }
            if (activo) {
                System.out.println();
            }
        }
        System.out.println("Fin del programa.");
    }

    private void mostrarMenu() {
        System.out.println("""

                +------------------- MENU PRINCIPAL -------------------+
                  1. Registrar estudiante          6. Retirar matrícula
                  2. Registrar curso               7. Listar cursos de un estudiante
                  3. Buscar estudiante             8. Listar estudiantes de un curso
                  4. Buscar curso                  9. Listar todos los estudiantes
                  5. Matricular estudiante         10. Listar todos los cursos
                  0. Salir
                +--------------------------------------------------------+
                """);
    }

    private void registrarEstudiante() {
        ejecutar(() -> {
            String codigo = pedir("Código del estudiante");
            String nombre = pedir("Nombre del estudiante");
            System.out.println("Registrado: " + servicio.registrarEstudiante(codigo, nombre));
        });
    }

    private void registrarCurso() {
        ejecutar(() -> {
            String codigo = pedir("Código del curso");
            String nombre = pedir("Nombre del curso");
            int cupo = pedirEntero("Cupo del curso");
            System.out.println("Registrado: " + servicio.registrarCurso(codigo, nombre, cupo));
        });
    }

    private void buscarEstudiante() {
        ejecutar(() -> {
            String codigo = pedir("Código del estudiante");
            servicio.buscarEstudiante(codigo)
                    .ifPresentOrElse(
                            estudiante -> System.out.println("Encontrado: " + estudiante),
                            () -> System.out.println("No existe un estudiante con ese código."));
        });
    }

    private void buscarCurso() {
        ejecutar(() -> {
            String codigo = pedir("Código del curso");
            servicio.buscarCurso(codigo)
                    .ifPresentOrElse(
                            curso -> System.out.println("Encontrado: " + curso),
                            () -> System.out.println("No existe un curso con ese código."));
        });
    }

    private void matricular() {
        ejecutar(() -> {
            String codigoEstudiante = pedir("Código del estudiante");
            String codigoCurso = pedir("Código del curso");
            System.out.println("Matriculado: " + servicio.matricular(codigoEstudiante, codigoCurso));
        });
    }

    private void retirar() {
        ejecutar(() -> {
            String codigoEstudiante = pedir("Código del estudiante");
            String codigoCurso = pedir("Código del curso");
            System.out.println("Retirada: " + servicio.retirar(codigoEstudiante, codigoCurso));
        });
    }

    private void listarCursosDeEstudiante() {
        ejecutar(() -> {
            String codigo = pedir("Código del estudiante");
            List<Curso> cursos = servicio.listarCursos(codigo);
            System.out.println("Cursos de " + codigo + (cursos.isEmpty() ? ": ninguno" : ": " + cursos));
        });
    }

    private void listarEstudiantesDeCurso() {
        ejecutar(() -> {
            String codigo = pedir("Código del curso");
            List<Estudiante> estudiantes = servicio.listarEstudiantes(codigo);
            System.out.println(
                    "Estudiantes de " + codigo + (estudiantes.isEmpty() ? ": ninguno" : ": " + estudiantes));
        });
    }

    private void listarEstudiantes() {
        List<Estudiante> estudiantes = servicio.listarEstudiantes();
        if (estudiantes.isEmpty()) {
            System.out.println("No hay estudiantes registrados.");
            return;
        }
        System.out.println("Estudiantes registrados (" + estudiantes.size() + "):");
        estudiantes.forEach(estudiante -> System.out.println("  " + estudiante));
    }

    private void listarCursos() {
        List<Curso> cursos = servicio.listarCursos();
        if (cursos.isEmpty()) {
            System.out.println("No hay cursos registrados.");
            return;
        }
        System.out.println("Cursos registrados (" + cursos.size() + "):");
        cursos.forEach(curso -> System.out.println("  " + curso));
    }

    private void ejecutar(Runnable operacion) {
        try {
            operacion.run();
        } catch (ReglaDeNegocioException excepcion) {
            System.out.println("Error: " + excepcion.getMessage());
        }
    }

    private String pedir(String mensaje) {
        System.out.print(mensaje + ": ");
        return entrada.nextLine();
    }

    private int pedirEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(pedir(mensaje));
            } catch (NumberFormatException excepcion) {
                System.out.println("Ingrese un número entero válido.");
            }
        }
    }
}