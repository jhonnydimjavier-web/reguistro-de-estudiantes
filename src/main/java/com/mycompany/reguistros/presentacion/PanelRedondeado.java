package com.mycompany.reguistros.presentacion;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Panel de esquinas redondeadas: la base de todas las "tarjetas" de la ventana. Si no se
 * indica un color propio, usa el de la superficie o el del borde del tema activo.
 */
public class PanelRedondeado extends JPanel {

    private final int radio;
    private Color relleno;
    private Color colorBorde;
    private boolean sombra;

    public PanelRedondeado(LayoutManager layout) {
        super(layout);
        this.radio = Tema.radio();
        setOpaque(false);
    }

    /** Un color {@code null} devuelve el color del tema activo. */
    public PanelRedondeado setRelleno(Color relleno) {
        this.relleno = relleno;
        repaint();
        return this;
    }

    /** Un color {@code null} devuelve el color del tema activo. */
    public PanelRedondeado setColorBorde(Color colorBorde) {
        this.colorBorde = colorBorde;
        repaint();
        return this;
    }

    public PanelRedondeado setSombra(boolean sombra) {
        this.sombra = sombra;
        repaint();
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int ancho = getWidth();
        int alto = getHeight();
        int diametro = radio * 2;
        Color fondo = relleno == null ? Tema.superficie() : relleno;
        Color borde = colorBorde == null ? Tema.borde() : colorBorde;

        Graphics2D lienzo = (Graphics2D) g.create();
        lienzo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        lienzo.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        if (sombra) {
            lienzo.setColor(Tema.sombra());
            lienzo.fillRoundRect(2, 3, ancho - 3, alto - 3, diametro, diametro);
        }
        lienzo.setColor(fondo);
        lienzo.fillRoundRect(0, 0, ancho, alto, diametro, diametro);
        lienzo.setColor(borde);
        lienzo.drawRoundRect(0, 0, ancho - 1, alto - 1, diametro, diametro);
        lienzo.dispose();
        super.paintComponent(g);
    }
}