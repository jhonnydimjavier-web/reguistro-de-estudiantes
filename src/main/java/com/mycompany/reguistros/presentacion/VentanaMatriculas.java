package com.mycompany.reguistros.presentacion;

import com.mycompany.reguistros.aplicacion.MatriculaService;
import com.mycompany.reguistros.dominio.Curso;
import com.mycompany.reguistros.dominio.Estudiante;
import com.mycompany.reguistros.dominio.Matricula;
import com.mycompany.reguistros.dominio.excepcion.CursoNoEncontradoException;
import com.mycompany.reguistros.dominio.excepcion.CursoYaRegistradoException;
import com.mycompany.reguistros.dominio.excepcion.CupoAgotadoException;
import com.mycompany.reguistros.dominio.excepcion.DatosInvalidosException;
import com.mycompany.reguistros.dominio.excepcion.EstudianteNoEncontradoException;
import com.mycompany.reguistros.dominio.excepcion.EstudianteYaRegistradoException;
import com.mycompany.reguistros.dominio.excepcion.LimiteCursosAlcanzadoException;
import com.mycompany.reguistros.dominio.excepcion.MatriculaDuplicadaException;
import com.mycompany.reguistros.dominio.excepcion.MatriculaNoEncontradaException;
import com.mycompany.reguistros.dominio.excepcion.ReglaDeNegocioException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class VentanaMatriculas extends JFrame {

    private static final String[] COLUMNAS_ESTUDIANTES = {"Código", "Nombre"};
    private static final String[] COLUMNAS_CURSOS = {"Código", "Nombre", "Cupo", "Matriculados", "Plazas"};
    private static final String[] COLUMNAS_MATRICULAS = {"Estudiante", "Curso", "Fecha"};

    /** Par etiqueta + editor de un campo del formulario. */
    private record Campo(String etiqueta, CampoTexto editor) {
    }

    private enum Aviso {
        INFO, EXITO, ERROR
    }

    private final MatriculaService servicio;

    private final DefaultTableModel modeloEstudiantes = new DefaultTableModel(COLUMNAS_ESTUDIANTES, 0);
    private final DefaultTableModel modeloCursos = new DefaultTableModel(COLUMNAS_CURSOS, 0);
    private final DefaultTableModel modeloMatriculas = new DefaultTableModel(COLUMNAS_MATRICULAS, 0);

    private final TableRowSorter<DefaultTableModel> sorterEstudiantes = new TableRowSorter<>(modeloEstudiantes);
    private final TableRowSorter<DefaultTableModel> sorterCursos = new TableRowSorter<>(modeloCursos);
    private final TableRowSorter<DefaultTableModel> sorterMatriculas = new TableRowSorter<>(modeloMatriculas);

    private final TablaEstilo tablaEstudiantes = new TablaEstilo(modeloEstudiantes);
    private final TablaEstilo tablaCursos = new TablaEstilo(modeloCursos);
    private final TablaEstilo tablaMatriculas = new TablaEstilo(modeloMatriculas);

    private final CampoTexto campoBusqueda = new CampoTexto(20, "Buscar en las tres tablas…");
    private final CampoTexto campoCodigoEstudiante = new CampoTexto(8, "E001");
    private final CampoTexto campoNombreEstudiante = new CampoTexto(16, "Nombre completo");
    private final CampoTexto campoCodigoCurso = new CampoTexto(8, "POO");
    private final CampoTexto campoNombreCurso = new CampoTexto(16, "Nombre del curso");
    private final CampoTexto campoCupoCurso = new CampoTexto(4, "30");
    private final CampoTexto campoMatriculaEstudiante = new CampoTexto(8, "E001");
    private final CampoTexto campoMatriculaCurso = new CampoTexto(8, "POO");

    private final Indicador indicadorEstudiantes = new Indicador("Estudiantes", "0");
    private final Indicador indicadorCursos = new Indicador("Cursos", "0");
    private final Indicador indicadorMatriculas = new Indicador("Matrículas", "0");
    private final Indicador indicadorPlazas = new Indicador("Plazas libres", "0");

    private final JLabel puntoEstado = new JLabel();
    private final Rotulo mensajeEstado = Rotulo.estado("Listo para empezar.");
    private final Rotulo contadorEstudiantes = Rotulo.contador("");
    private final Rotulo contadorCursos = Rotulo.contador("");
    private final Rotulo contadorMatriculas = Rotulo.contador("");
    private final Rotulo pieResumen = Rotulo.pie("");

    private Aviso ultimoAviso = Aviso.INFO;

    public VentanaMatriculas(MatriculaService servicio) {
        this.servicio = servicio;
        configurarVentana();
        conectarEventos();
        refrescar();
    }

    private void configurarVentana() {
        setTitle("Sistema de Matrículas · %s".formatted(DemoAceptacion.PERIODO));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1080, 680));
        setSize(1360, 920);
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Tema.fondo());
        raiz.setBorder(new EmptyBorder(18, 22, 12, 22));
        raiz.add(crearEncabezado(), BorderLayout.NORTH);
        raiz.add(crearCuerpo(), BorderLayout.CENTER);
        raiz.add(crearPie(), BorderLayout.SOUTH);
        setContentPane(raiz);
    }

    private JComponent crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout(18, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 2, 18, 2));

        JPanel textos = new JPanel(new BorderLayout(0, 4));
        textos.setOpaque(false);
        textos.add(Rotulo.titulo("Sistema de Matrículas"), BorderLayout.NORTH);
        textos.add(Rotulo.subtitulo("Periodo %s · máximo %d cursos activos por estudiante"
                .formatted(DemoAceptacion.PERIODO, MatriculaService.MAX_CURSOS_ACTIVOS_POR_ESTUDIANTE)),
                BorderLayout.SOUTH);

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        busqueda.setOpaque(false);
        busqueda.setPreferredSize(new Dimension(340, 42));
        campoBusqueda.setToolTipText("Filtra las tres tablas a la vez");
        busqueda.add(campoBusqueda);

        Boton tema = new Boton("Modo oscuro", Boton.Variante.SECUNDARIO);
        tema.setToolTipText("Cambia el aspecto de la aplicación");
        tema.addActionListener(evento -> {
            Tema destino = Tema.activo() == Tema.CLARO ? Tema.OSCURO : Tema.CLARO;
            Tema.activar(destino);
            tema.setText(destino == Tema.CLARO ? "Modo oscuro" : "Modo claro");
            refrescar();
        });

        Boton refrescar = new Boton("Refrescar", Boton.Variante.SECUNDARIO);
        refrescar.setToolTipText("Vuelve a leer los datos del servicio");
        refrescar.addActionListener(evento -> {
            refrescar();
            avisar("Datos actualizados.", Aviso.INFO);
        });

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);
        acciones.add(refrescar);
        acciones.add(tema);

        panel.add(textos, BorderLayout.WEST);
        panel.add(busqueda, BorderLayout.CENTER);
        panel.add(acciones, BorderLayout.EAST);
        return panel;
    }

    private JComponent crearCuerpo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel indicadores = new JPanel(new GridLayout(1, 4, 14, 0));
        indicadores.setOpaque(false);
        indicadores.add(indicadorEstudiantes);
        indicadores.add(indicadorCursos);
        indicadores.add(indicadorMatriculas);
        indicadores.add(indicadorPlazas);

        JPanel parteFija = new JPanel(new BorderLayout(0, 14));
        parteFija.setOpaque(false);
        parteFija.add(crearFormulario(), BorderLayout.NORTH);
        parteFija.add(indicadores, BorderLayout.SOUTH);
        panel.add(parteFija, BorderLayout.NORTH);

        JPanel tablas = new JPanel(new GridBagLayout());
        tablas.setOpaque(false);
        tablas.add(crearTarjeta("Estudiantes", tablaEstudiantes, contadorEstudiantes), celda(0, 0.40));
        tablas.add(crearTarjeta("Cursos", tablaCursos, contadorCursos), celda(1, 0.60));

        JComponent matriculas = crearTarjeta("Matrículas", tablaMatriculas, contadorMatriculas);
        matriculas.setPreferredSize(new Dimension(0, 230));

        JSplitPane divisor = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tablas, matriculas);
        divisor.setResizeWeight(0.68);
        divisor.setContinuousLayout(true);
        divisor.setDividerSize(14);
        divisor.setBorder(BorderFactory.createEmptyBorder());
        divisor.setOpaque(false);
        divisor.setMinimumSize(new Dimension(0, 240));
        panel.add(divisor, BorderLayout.CENTER);
        return panel;
    }

    private GridBagConstraints celda(int columna, double peso) {
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.gridx = columna;
        restricciones.gridy = 0;
        restricciones.weightx = peso;
        restricciones.weighty = 1.0;
        restricciones.fill = GridBagConstraints.BOTH;
        restricciones.insets = new Insets(0, columna == 0 ? 0 : 14, 0, columna == 0 ? 14 : 0);
        return restricciones;
    }

    private JComponent crearFormulario() {
        Boton registrarEstudiante = new Boton("Registrar", Boton.Variante.PRIMARIO);
        registrarEstudiante.addActionListener(evento -> registrarEstudiante());
        Boton registrarCurso = new Boton("Registrar", Boton.Variante.PRIMARIO);
        registrarCurso.addActionListener(evento -> registrarCurso());
        Boton matricular = new Boton("Matricular", Boton.Variante.PRIMARIO);
        matricular.addActionListener(evento -> matricular());
        Boton retirar = new Boton("Retirar", Boton.Variante.PELIGRO);
        retirar.addActionListener(evento -> retirar());

        JPanel panel = new JPanel(new GridLayout(1, 3, 14, 0));
        panel.setOpaque(false);
        panel.add(crearGrupo("Registrar estudiante",
                List.of(new Campo("Código", campoCodigoEstudiante),
                        new Campo("Nombre", campoNombreEstudiante)),
                registrarEstudiante));
        panel.add(crearGrupo("Registrar curso",
                List.of(new Campo("Código", campoCodigoCurso),
                        new Campo("Nombre", campoNombreCurso),
                        new Campo("Cupo", campoCupoCurso)),
                registrarCurso));
        panel.add(crearGrupo("Matricular o retirar",
                List.of(new Campo("Estudiante", campoMatriculaEstudiante),
                        new Campo("Curso", campoMatriculaCurso)),
                List.of(matricular, retirar)));
        return panel;
    }

    private JComponent crearGrupo(String titulo, List<Campo> campos, Object acciones) {
        PanelRedondeado tarjeta = new PanelRedondeado(new BorderLayout(0, 14));
        tarjeta.setBorder(new EmptyBorder(16, 18, 18, 18));

        JPanel cabecera = new JPanel(new BorderLayout(10, 0));
        cabecera.setOpaque(false);
        cabecera.add(Rotulo.seccion(titulo), BorderLayout.WEST);
        cabecera.add(crearAcciones(acciones), BorderLayout.EAST);
        tarjeta.add(cabecera, BorderLayout.NORTH);

        JPanel fila = new JPanel(new GridBagLayout());
        fila.setOpaque(false);
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.gridy = 0;
        restricciones.anchor = GridBagConstraints.WEST;
        restricciones.fill = GridBagConstraints.HORIZONTAL;
        restricciones.insets = new Insets(0, 0, 0, 10);
        for (int i = 0; i < campos.size(); i++) {
            restricciones.gridx = i;
            restricciones.weightx = 1.0 / campos.size();
            fila.add(crearEditor(campos.get(i)), restricciones);
        }
        tarjeta.add(fila, BorderLayout.CENTER);
        return tarjeta;
    }

    private JComponent crearEditor(Campo campo) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        campo.editor().setPreferredSize(new Dimension(80, 40));
        panel.add(Rotulo.etiquetaDeCampo(campo.etiqueta()), BorderLayout.NORTH);
        panel.add(campo.editor(), BorderLayout.CENTER);
        return panel;
    }

    private JComponent crearAcciones(Object acciones) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.setOpaque(false);
        List<Boton> botones = switch (acciones) {
            case Boton boton -> List.of(boton);
            case List<?> lista -> lista.stream().map(Boton.class::cast).toList();
            default -> List.of();
        };
        botones.forEach(panel::add);
        return panel;
    }

    private JComponent crearTarjeta(String titulo, TablaEstilo tabla, Rotulo contador) {
        PanelRedondeado tarjeta = new PanelRedondeado(new BorderLayout());

        JPanel cabecera = new JPanel(new BorderLayout(10, 0));
        cabecera.setOpaque(false);
        cabecera.setBorder(new EmptyBorder(16, 18, 14, 18));
        cabecera.add(Rotulo.seccion(titulo), BorderLayout.WEST);
        cabecera.add(contador, BorderLayout.EAST);

        JScrollPane desplazamiento = BarraDesplazamiento.aplicar(new JScrollPane(tabla), 10);
        desplazamiento.getViewport().setBackground(Tema.superficie());

        tarjeta.add(cabecera, BorderLayout.NORTH);
        tarjeta.add(desplazamiento, BorderLayout.CENTER);
        return tarjeta;
    }

    private JComponent crearPie() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(16, 4, 0, 4));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 2));
        izquierda.setOpaque(false);
        puntoEstado.setOpaque(true);
        puntoEstado.setPreferredSize(new Dimension(9, 9));
        izquierda.add(puntoEstado);
        izquierda.add(mensajeEstado);

        panel.add(izquierda, BorderLayout.WEST);
        panel.add(pieResumen, BorderLayout.EAST);
        return panel;
    }

    private void conectarEventos() {
        tablaEstudiantes.setColumnaClave(0);
        tablaCursos.setColumnaClave(0);
        tablaCursos.setColumnasDerecha(2, 3, 4);
        tablaMatriculas.setColumnaClave(0);

        // el tamaño preferido decide el reparto inicial del divisor; el ancho no puede
        // ser 0 porque el ancho del viewport forma parte del tamaño preferido
        tablaEstudiantes.setPreferredScrollableViewportSize(new Dimension(260, 250));
        tablaCursos.setPreferredScrollableViewportSize(new Dimension(360, 250));
        tablaMatriculas.setPreferredScrollableViewportSize(new Dimension(640, 170));

        alHacerClic(tablaEstudiantes, fila -> campoMatriculaEstudiante.setText(valorCelda(tablaEstudiantes, fila, 0)));
        alHacerClic(tablaCursos, fila -> campoMatriculaCurso.setText(valorCelda(tablaCursos, fila, 0)));
        alHacerClic(tablaMatriculas, fila -> {
            campoMatriculaEstudiante.setText(valorCelda(tablaMatriculas, fila, 0));
            campoMatriculaCurso.setText(valorCelda(tablaMatriculas, fila, 1));
        });

        alDobleClic(tablaMatriculas, fila -> {
            campoMatriculaEstudiante.setText(valorCelda(tablaMatriculas, fila, 0));
            campoMatriculaCurso.setText(valorCelda(tablaMatriculas, fila, 1));
            avisar("Parámetros listos para retirar.", Aviso.INFO);
        });

        campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent evento) {
                aplicarFiltro();
            }

            @Override
            public void removeUpdate(DocumentEvent evento) {
                aplicarFiltro();
            }

            @Override
            public void changedUpdate(DocumentEvent evento) {
                aplicarFiltro();
            }
        });

        enterEn(campoNombreEstudiante, this::registrarEstudiante);
        enterEn(campoCupoCurso, this::registrarCurso);
        enterEn(campoMatriculaEstudiante, this::matricular);
        enterEn(campoMatriculaCurso, this::matricular);
    }

    private void alHacerClic(TablaEstilo tabla, Consumer<Integer> accion) {
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    accion.accept(fila);
                }
            }
        });
    }

    private void alDobleClic(TablaEstilo tabla, Consumer<Integer> accion) {
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                int fila = tabla.getSelectedRow();
                if (evento.getClickCount() == 2 && fila >= 0) {
                    accion.accept(fila);
                }
            }
        });
    }

    private void enterEn(JComponent campo, Runnable accion) {
        campo.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "aceptar");
        campo.getActionMap().put("aceptar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evento) {
                accion.run();
            }
        });
    }

    private String valorCelda(TablaEstilo tabla, int fila, int columna) {
        Object valor = tabla.getModel().getValueAt(tabla.convertRowIndexToModel(fila), columna);
        return valor == null ? "" : String.valueOf(valor);
    }

    private void registrarEstudiante() {
        try {
            servicio.registrarEstudiante(texto(campoCodigoEstudiante), texto(campoNombreEstudiante));
            campoCodigoEstudiante.setText("");
            campoNombreEstudiante.setText("");
            avisar("Estudiante registrado.", Aviso.EXITO);
        } catch (ReglaDeNegocioException excepcion) {
            fallo(excepcion);
        }
    }

    private void registrarCurso() {
        try {
            int cupo = Integer.parseInt(texto(campoCupoCurso));
            servicio.registrarCurso(texto(campoCodigoCurso), texto(campoNombreCurso), cupo);
            campoCodigoCurso.setText("");
            campoNombreCurso.setText("");
            campoCupoCurso.setText("");
            avisar("Curso registrado.", Aviso.EXITO);
        } catch (NumberFormatException excepcion) {
            fallo(DatosInvalidosException.de("El cupo debe ser un número entero."));
        } catch (ReglaDeNegocioException excepcion) {
            fallo(excepcion);
        }
    }

    private void matricular() {
        try {
            Matricula matricula = servicio.matricular(
                    texto(campoMatriculaEstudiante), texto(campoMatriculaCurso));
            campoMatriculaEstudiante.setText("");
            campoMatriculaCurso.setText("");
            avisar("%s se matriculó en %s.".formatted(
                    matricula.getEstudiante().getNombre(), matricula.getCurso().getNombre()), Aviso.EXITO);
        } catch (ReglaDeNegocioException excepcion) {
            fallo(excepcion);
        }
    }

    private void retirar() {
        try {
            Matricula matricula = servicio.retirar(texto(campoMatriculaEstudiante), texto(campoMatriculaCurso));
            campoMatriculaEstudiante.setText("");
            campoMatriculaCurso.setText("");
            avisar("Se retiró la matrícula de %s en %s.".formatted(
                    matricula.getEstudiante().getCodigo(), matricula.getCurso().getCodigo()), Aviso.EXITO);
        } catch (ReglaDeNegocioException excepcion) {
            fallo(excepcion);
        }
    }

    private void fallo(ReglaDeNegocioException excepcion) {
        avisar(excepcion.getMessage() + " " + consejo(excepcion), Aviso.ERROR);
    }

    /**
     * Ayuda contextual según el fallo concreto. Como {@code ReglaDeNegocioException} es
     * {@code sealed}, este {@code switch} es exhaustivo y no necesita {@code default}:
     * si mañana se añade una excepción nueva, el compilador avisa.
     */
    private String consejo(ReglaDeNegocioException excepcion) {
        return switch (excepcion) {
            case CupoAgotadoException ignored -> "Prueba con un curso que tenga plazas libres.";
            case LimiteCursosAlcanzadoException ignored -> "Retira una matrícula para liberar un cupo del estudiante.";
            case MatriculaDuplicadaException ignored -> "Esa pareja ya aparece en la tabla de matrículas.";
            case MatriculaNoEncontradaException ignored -> "Selecciona la fila de la tabla y pulsa Retirar.";
            case DatosInvalidosException ignored -> "Revisa los campos del formulario.";
            case EstudianteNoEncontradoException ignored -> "El código debe estar en la tabla de estudiantes.";
            case CursoNoEncontradoException ignored -> "El código debe estar en la tabla de cursos.";
            case EstudianteYaRegistradoException ignored -> "Usa otro código o quita el filtro de búsqueda.";
            case CursoYaRegistradoException ignored -> "Usa otro código o quita el filtro de búsqueda.";
        };
    }

    private void refrescar() {
        rellenarEstudiantes();
        rellenarCursos();
        rellenarMatriculas();
        aplicarFiltro();
        actualizarIndicadores();
        pintarPuntoEstado();
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
            int matriculados = matriculados(curso);
            modeloCursos.addRow(new Object[]{
                curso.getCodigo(),
                curso.getNombre(),
                curso.getCupo(),
                matriculados,
                curso.plazasDisponibles(matriculados)
            });
        }
    }

    private void rellenarMatriculas() {
        modeloMatriculas.setRowCount(0);
        for (Matricula matricula : servicio.listarMatriculas()) {
            modeloMatriculas.addRow(new Object[]{
                matricula.getEstudiante().getCodigo(),
                matricula.getCurso().getCodigo(),
                matricula.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE)
            });
        }
    }

    private void actualizarIndicadores() {
        int estudiantes = servicio.listarEstudiantes().size();
        int cursos = servicio.listarCursos().size();
        int matriculas = servicio.listarMatriculas().size();

        int plazas = 0;
        int completos = 0;
        for (Curso curso : servicio.listarCursos()) {
            int ocupados = matriculados(curso);
            plazas += curso.plazasDisponibles(ocupados);
            if (!curso.tieneCupoDisponible(ocupados)) {
                completos++;
            }
        }

        indicadorEstudiantes.setValor(String.valueOf(estudiantes), Tema.texto());
        indicadorCursos.setValor(String.valueOf(cursos), Tema.texto());
        indicadorMatriculas.setValor(String.valueOf(matriculas), Tema.texto());
        indicadorPlazas.setValor(String.valueOf(plazas), plazas > 0 ? Tema.exito() : Tema.peligro());

        pieResumen.setText("%d plazas libres de %d · %d de %d cursos con el cupo completo"
                .formatted(plazas, plazas + matriculas, completos, cursos));
    }

    private int matriculados(Curso curso) {
        return servicio.listarEstudiantes(curso.getCodigo()).size();
    }

    private void aplicarFiltro() {
        String texto = campoBusqueda.getText().trim();
        RowFilter<Object, Object> filtro = texto.isEmpty()
                ? null
                : RowFilter.regexFilter("(?i)" + Pattern.quote(texto));
        sorterEstudiantes.setRowFilter(filtro);
        sorterCursos.setRowFilter(filtro);
        sorterMatriculas.setRowFilter(filtro);
        actualizarContadores();
    }

    private void actualizarContadores() {
        contadorEstudiantes.setText(contador(tablaEstudiantes, modeloEstudiantes));
        contadorCursos.setText(contador(tablaCursos, modeloCursos));
        contadorMatriculas.setText(contador(tablaMatriculas, modeloMatriculas));
    }

    private String contador(TablaEstilo tabla, DefaultTableModel modelo) {
        int total = modelo.getRowCount();
        return tabla.getRowCount() == total ? String.valueOf(total) : tabla.getRowCount() + " / " + total;
    }

    private void avisar(String texto, Aviso tipo) {
        ultimoAviso = tipo;
        mensajeEstado.setText(texto);
        pintarPuntoEstado();
    }

    private void pintarPuntoEstado() {
        Color color = switch (ultimoAviso) {
            case EXITO -> Tema.exito();
            case ERROR -> Tema.peligro();
            case INFO -> Tema.acento();
        };
        puntoEstado.setBackground(color);
        puntoEstado.setOpaque(true);
        puntoEstado.repaint();
    }

    private String texto(JTextField campo) {
        return campo.getText().trim();
    }
}