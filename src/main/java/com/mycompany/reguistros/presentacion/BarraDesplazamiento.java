package com.mycompany.reguistros.presentacion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * Barra de desplazamiento fina, sin botones y con pulgar redondeado, que se integra con el
 * tema activo.
 */
public class BarraDesplazamiento extends BasicScrollBarUI {

    private final int grosor;

    public BarraDesplazamiento(int grosor) {
        this.grosor = grosor;
    }

    /** Deja un {@link JScrollPane} sin marco y con las barras de este estilo. */
    public static JScrollPane aplicar(JScrollPane panel, int grosor) {
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        panel.setViewportBorder(null);
        panel.getVerticalScrollBar().setUI(new BarraDesplazamiento(grosor));
        panel.getHorizontalScrollBar().setUI(new BarraDesplazamiento(grosor));
        panel.getVerticalScrollBar().setOpaque(false);
        panel.getHorizontalScrollBar().setOpaque(false);
        panel.getVerticalScrollBar().setPreferredSize(new Dimension(grosor + 8, 0));
        panel.getHorizontalScrollBar().setPreferredSize(new Dimension(0, grosor + 8));
        return panel;
    }

    @Override
    protected JButton createIncreaseButton(int orientacion) {
        return botonSinPintar();
    }

    @Override
    protected JButton createDecreaseButton(int orientacion) {
        return botonSinPintar();
    }

    /**
     * Los botones de incremento y decremento se crean con tamaño cero: existen porque el
     * layout de {@code BasicScrollBarUI} los consulta siempre, pero no ocupan espacio ni se
     * dibujan.
     */
    private static JButton botonSinPintar() {
        return new JButton() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, 0);
            }

            @Override
            public Dimension getMinimumSize() {
                return new Dimension(0, 0);
            }

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(0, 0);
            }
        };
    }

    @Override
    protected void configureScrollBarColors() {
        trackColor = Tema.fondo();
        thumbColor = Tema.borde();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent componente, Rectangle limites) {
        g.setColor(Tema.fondo());
        g.fillRect(limites.x, limites.y, limites.width, limites.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent componente, Rectangle limites) {
        if (limites.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
        Graphics2D lienzo = (Graphics2D) g.create();
        lienzo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color color = isDragging || isThumbRollover() ? Tema.textoTenue() : Tema.borde();
        lienzo.setColor(color);
        int margen = 5;
        lienzo.fillRoundRect(limites.x + margen, limites.y + margen,
                limites.width - margen * 2, limites.height - margen * 2, 12, 12);
        lienzo.dispose();
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(grosor + 14, grosor + 14);
    }
}