package com.mycompany.reguistros.presentacion;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * Tabla sin cuadrícula, con filas en zigzag, fila resaltada al pasar el ratón, cabecera en
 * versalitas y valores numéricos alineados a la derecha.
 */
public class TablaEstilo extends JTable {

    private int filaEncima = -1;
    private int columnaClave = -1;
    private final Set<Integer> columnasDerecha = new HashSet<>();

    public TablaEstilo(DefaultTableModel modelo) {
        super(modelo);
        aplicarEstilo();
        instalarFilaEncima();
    }

    private void aplicarEstilo() {
        setRowHeight(38);
        setShowHorizontalLines(false);
        setShowVerticalLines(false);
        setGridColor(Tema.fondo());
        setIntercellSpacing(new Dimension(0, 0));
        setFillsViewportHeight(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        setFont(Tema.fuente(14f));
        setBackground(Tema.superficie());
        setForeground(Tema.texto());
        setSelectionBackground(Tema.seleccion());
        setSelectionForeground(Tema.seleccionTexto());
        setDefaultRenderer(Object.class, renderer());
        getTableHeader().setDefaultRenderer(rendererCabecera());
        aplicarEstiloCabecera();
    }

    private void aplicarEstiloCabecera() {
        JTableHeader cabecera = getTableHeader();
        cabecera.setReorderingAllowed(false);
        cabecera.setResizingAllowed(false);
        cabecera.setBorder(BorderFactory.createEmptyBorder());
        cabecera.setBackground(Tema.cabecera());
        cabecera.setForeground(Tema.cabeceraTexto());
        cabecera.setFont(Tema.fuente(11.5f, Font.BOLD));
        cabecera.setPreferredSize(new Dimension(0, 40));
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (getTableHeader() != null) {
            aplicarEstilo();
        }
    }

    private void instalarFilaEncima() {
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent evento) {
                fijarFilaEncima(rowAtPoint(evento.getPoint()));
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent evento) {
                fijarFilaEncima(-1);
            }
        });
    }

    public void fijarFilaEncima(int fila) {
        if (fila != filaEncima) {
            filaEncima = fila;
            repaint();
        }
    }

    /** Columna que se dibuja en negrita, normalmente el código. */
    public void setColumnaClave(int columna) {
        this.columnaClave = columna;
    }

    public void setColumnasDerecha(int... columnas) {
        columnasDerecha.clear();
        for (int columna : columnas) {
            columnasDerecha.add(columna);
        }
    }

    private TableCellRenderer renderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionado,
                    boolean enfocado, int fila, int columna) {
                boolean derecha = columnasDerecha.contains(columna);
                Component celda = super.getTableCellRendererComponent(
                        tabla, valor, seleccionado, enfocado, fila, columna);
                setHorizontalAlignment(derecha ? RIGHT : LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 16, 0, derecha ? 16 : 10));
                if (columna == columnaClave) {
                    setFont(Tema.fuente(14f, Font.BOLD));
                }
                if (seleccionado) {
                    setBackground(Tema.seleccion());
                    setForeground(Tema.seleccionTexto());
                } else {
                    setBackground(fila == filaEncima ? Tema.acentoTenue()
                            : (fila % 2 == 0 ? Tema.superficie() : Tema.filaPar()));
                    setForeground(Tema.texto());
                }
                return this;
            }
        };
    }

    private TableCellRenderer rendererCabecera() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionado,
                    boolean enfocado, int fila, int columna) {
                boolean derecha = columnasDerecha.contains(columna);
                Component celda = super.getTableCellRendererComponent(
                        tabla, valor, seleccionado, enfocado, fila, columna);
                setFont(Tema.fuente(11.5f, Font.BOLD));
                setBackground(Tema.cabecera());
                setForeground(Tema.cabeceraTexto());
                setHorizontalAlignment(derecha ? RIGHT : LEFT);
                setBorder(new MatteBorder(0, derecha ? 0 : 16, 1, derecha ? 16 : 10, Tema.borde()));
                return this;
            }
        };
    }
}