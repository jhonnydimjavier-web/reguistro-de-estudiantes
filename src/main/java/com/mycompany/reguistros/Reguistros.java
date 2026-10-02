package com.mycompany.reguistros;

import com.mycompany.reguistros.aplicacion.MatriculaService;
import com.mycompany.reguistros.presentacion.ConsolaMatriculas;
import com.mycompany.reguistros.presentacion.DemoAceptacion;
import com.mycompany.reguistros.presentacion.Tema;
import com.mycompany.reguistros.presentacion.VentanaMatriculas;
import com.mycompany.reguistros.repositorio.memoria.CursoEnMemoriaRepository;
import com.mycompany.reguistros.repositorio.memoria.EstudianteEnMemoriaRepository;
import com.mycompany.reguistros.repositorio.memoria.MatriculaEnMemoriaRepository;
import java.util.Arrays;
import javax.swing.SwingUtilities;

public class Reguistros {

    public static void main(String[] args) {
        MatriculaService servicio = crearServicio();
        if (Arrays.asList(args).contains("--consola")) {
            System.out.println("=== SISTEMA DE MATRICULAS (CONSOLA) ===");
            DemoAceptacion.mostrar(servicio);
            System.out.println();
            new ConsolaMatriculas(servicio).iniciar();
        } else {
            DemoAceptacion.cargar(servicio);
            Tema.preparar();
            SwingUtilities.invokeLater(() -> new VentanaMatriculas(servicio).setVisible(true));
        }
    }

    private static MatriculaService crearServicio() {
        return new MatriculaService(
                new EstudianteEnMemoriaRepository(),
                new CursoEnMemoriaRepository(),
                new MatriculaEnMemoriaRepository());
    }
}