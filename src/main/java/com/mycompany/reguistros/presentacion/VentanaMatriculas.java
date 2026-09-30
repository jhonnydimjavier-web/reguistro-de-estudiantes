package com.mycompany.reguistros.presentacion;

import com.mycompany.reguistros.aplicacion.MatriculaService;
import com.mycompany.reguistros.dominio.Curso;
import com.mycompany.reguistros.dominio.Estudiante;
import com.mycompany.reguistros.dominio.Matricula;
import com.mycompany.reguistros.dominio.excepcion.ReglaDeNegocioException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class VentanaMatriculas extends JFrame {

    private static final String[] COLUMNAS_ESTUDIANTES = {"Codigo", "Nombre"};
    private static final String[] COLUMNAS_CURSOS = {"Codigo", "Nombre", "Cupo", "Ocupacion", "Plazas libres"};
    private static final String[] COLUMNAS_MATRICULAS = {"Estudiante", "Curso", "Fecha"};

    private final MatriculaService servicio;
    private final DefaultTableModel modeloEstudiantes = modeloNoEditable(COLUMNAS_ESTUDIANTES);
    private final DefaultTableModel modeloCursos = modeloNoEditable(COLUMNAS_CURSOS);
    private final DefaultTableModel modeloMatriculas = modeloNoEditable(COLUMNAS_MATRICULAS);
    private final JLabel pie = new JLabel();

    public VentanaMatriculas(MatriculaService servicio) {
        this.servicio = servicio;
        configurarVentana();
        refrescar();
    }

    private void configurarVentana() {
        setTitle("Sistema de Matriculas");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(780, 460);
        setLocationRelativeTo(null);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Estudiantes", panelConTabla(modeloEstudiantes));
        pestanas.addTab("Cursos", panelConTabla(modeloCursos));
        pestanas.addTab("Matriculas", panelConTabla(modeloMatriculas));

        add(pestanas, BorderLayout.CENTER);
        add(panelBotones(), BorderLayout.NORTH);
        add(pie, BorderLayout.SOUTH);
    }

    private JScrollPane panelConTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        return new JScrollPane(tabla);
    }

    private JPanel panelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(boton("Registrar estudiante", this::registrarEstudiante));
        panel.add(boton("Registrar curso", this::registrarCurso));
        panel.add(boton("Matricular", this::matricular));
        panel.add(boton("Retirar matricula", this::retirar));
        panel.add(boton("Refrescar", this::refrescar));
        return panel;
    }

    private JButton boton(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(evento -> accion.run());
        return boton;
    }

    private void registrarEstudiante() {
        String codigo = pedir("Codigo del estudiante");
        if (codigo == null) {
            return;
        }
        String nombre = pedir("Nombre del estudiante");
        if (nombre == null) {
            return;
        }
        try {
            servicio.registrarEstudiante(codigo, nombre);
            refrescar();
        } catch (ReglaDeNegocioException excepcion) {
            mostrarError(excepcion);
        }
    }

    private void registrarCurso() {
        String codigo = pedir("Codigo del curso");
        if (codigo == null) {
            return;
        }
        String nombre = pedir("Nombre del curso");
        if (nombre == null) {
            return;
        }
        String cupo = pedir("Cupo del curso");
        if (cupo == null) {
            return;
        }
        try {
            servicio.registrarCurso(codigo, nombre, Integer.parseInt(cupo.trim()));
            refrescar();
        } catch (NumberFormatException excepcion) {
            mostrarError("El cupo debe ser un numero entero.");
        } catch (ReglaDeNegocioException excepcion) {
            mostrarError(excepcion);
        }
    }

    private void matricular() {
        String codigoEstudiante = pedir("Codigo del estudiante");
        if (codigoEstudiante == null) {
            return;
        }
        String codigoCurso = pedir("Codigo del curso");
        if (codigoCurso == null) {
            return;
        }
        try {
            servicio.matricular(codigoEstudiante, codigoCurso);
            refrescar();
        } catch (ReglaDeNegocioException excepcion) {
            mostrarError(excepcion);
        }
    }

    private void retirar() {
        String codigoEstudiante = pedir("Codigo del estudiante");
        if (codigoEstudiante == null) {
            return;
        }
        String codigoCurso = pedir("Codigo del curso");
        if (codigoCurso == null) {
            return;
        }
        try {
            servicio.retirar(codigoEstudiante, codigoCurso);
            refrescar();
        } catch (ReglaDeNegocioException excepcion) {
            mostrarError(excepcion);
        }
    }

    private void refrescar() {
        rellenarEstudiantes();
        rellenarCursos();
        rellenarMatriculas();
        actualizarPie();
    }

    private void rellenarEstudiantes() {
        modeloEstudiantes.setRowCount(0);
        for (Estudiante estudiante : servicio.listarEstudiantes()) {
            modeloEstudiantes.addRow(new Object[]{estudiante.getCodigo(), estudiante.getNombre()});
        }
    }

    private void rellenarCursos() {
        modeloCursos.setRowCount(0);
        for (Curso curso : servicio.listarCursos()) {
            int ocupacion = servicio.listarEstudiantes(curso.getCodigo()).size();
            modeloCursos.addRow(new Object[]{
                curso.getCodigo(),
                curso.getNombre(),
                curso.getCupo(),
                ocupacion,
                curso.plazasDisponibles(ocupacion)
            });
        }
    }

    private void rellenarMatriculas() {
        modeloMatriculas.setRowCount(0);
        for (Matricula matricula : servicio.listarMatriculas()) {
            modeloMatriculas.addRow(new Object[]{
                matricula.getEstudiante().getCodigo(),
                matricula.getCurso().getCodigo(),
                matricula.getFecha()
            });
        }
    }

    private void actualizarPie() {
        pie.setText("Estudiantes: " + servicio.listarEstudiantes().size()
                + "   Cursos: " + servicio.listarCursos().size()
                + "   Matriculas: " + servicio.listarMatriculas().size()
                + "   Maximo de cursos por estudiante: " + MatriculaService.MAX_CURSOS_ACTIVOS_POR_ESTUDIANTE);
    }

    private String pedir(String mensaje) {
        return JOptionPane.showInputDialog(this, mensaje);
    }

    private void mostrarError(ReglaDeNegocioException excepcion) {
        mostrarError(excepcion.getMessage());
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static DefaultTableModel modeloNoEditable(String[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }
}