package com.mycompany.reguistros.presentacion;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/**
 * Botón plano con esquinas redondeadas y cuatro variantes. Todos los colores se deciden al
 * pintar para que el cambio de tema sea inmediato.
 */
public class Boton extends JButton {

    public enum Variante {
        PRIMARIO, SECUNDARIO, PELIGRO, FANTASMA
    }

    private static final int ALTO = 40;

    private final Variante variante;
    private boolean encima;
    private boolean pulsado;

    public Boton(String texto, Variante variante) {
        super(texto);
        this.variante = variante;
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setMargin(new Insets(0, 0, 0, 0));
        setBorder(javax.swing.BorderFactory.createEmptyBorder());
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(Tema.fuente(13.5f, Font.BOLD));
        instalarEstados();
    }

    private void instalarEstados() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evento) {
                encima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                encima = false;
                pulsado = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent evento) {
                pulsado = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent evento) {
                pulsado = false;
                repaint();
            }
        });
    }

    private Color colorFondo() {
        if (!isEnabled()) {
            return variante == Variante.PRIMARIO ? null : Tema.fondo();
        }
        boolean activo = encima || pulsado;
        return switch (variante) {
            case PRIMARIO -> activo ? Tema.acentoHover() : Tema.acento();
            case SECUNDARIO -> activo ? Tema.acentoTenue() : Tema.superficie();
            case PELIGRO -> activo ? Tema.peligroTenue() : Tema.superficie();
            case FANTASMA -> activo ? Tema.acentoTenue() : null;
        };
    }

    private Color colorTexto() {
        if (!isEnabled()) {
            return Tema.textoTenue();
        }
        return switch (variante) {
            case PRIMARIO -> java.awt.Color.WHITE;
            case SECUNDARIO, FANTASMA -> Tema.acento();
            case PELIGRO -> Tema.peligro();
        };
    }

    private Color colorBorde() {
        if (!isEnabled()) {
            return Tema.borde();
        }
        return switch (variante) {
            case PRIMARIO, FANTASMA -> null;
            case SECUNDARIO -> encima ? Tema.acento() : Tema.borde();
            case PELIGRO -> encima ? Tema.peligro() : Tema.peligroTenue();
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        int ancho = getWidth();
        int alto = getHeight();
        int radio = Tema.radioPequeno();
        int diametro = radio * 2;

        Graphics2D lienzo = (Graphics2D) g.create();
        lienzo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fondo = colorFondo();
        if (fondo != null) {
            lienzo.setColor(fondo);
            lienzo.fillRoundRect(0, 0, ancho, alto, diametro, diametro);
        }
        Color borde = colorBorde();
        if (borde != null) {
            lienzo.setColor(borde);
            lienzo.drawRoundRect(0, 0, ancho - 1, alto - 1, diametro, diametro);
        }
        lienzo.dispose();

        setForeground(colorTexto());
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        int ancho = Math.max(96, getFontMetrics(getFont()).stringWidth(getText()) + 36);
        return new Dimension(ancho, ALTO);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}