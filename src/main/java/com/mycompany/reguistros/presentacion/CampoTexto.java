package com.mycompany.reguistros.presentacion;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Campo de texto con fondo redondeado, borde que se ilumina al escribir y texto de ayuda
 * cuando está vacío.
 */
public class CampoTexto extends JTextField {

    private final String ayuda;
    private Runnable alCambiar;

    public CampoTexto(int columnas, String ayuda) {
        super(columnas);
        this.ayuda = ayuda;
        updateUI();
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent evento) {
                avisar();
            }

            @Override
            public void removeUpdate(DocumentEvent evento) {
                avisar();
            }

            @Override
            public void changedUpdate(DocumentEvent evento) {
                avisar();
            }
        });
    }

    public void setAlCambiar(Runnable alCambiar) {
        this.alCambiar = alCambiar;
    }

    private void avisar() {
        if (alCambiar != null) {
            alCambiar.run();
        }
        repaint();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setOpaque(false);
        setBorder(new EmptyBorder(0, 12, 0, 12));
        setFont(Tema.fuente(14f));
        setForeground(Tema.texto());
        setCaretColor(Tema.acento());
        setSelectionColor(Tema.seleccion());
        setSelectedTextColor(Tema.seleccionTexto());
    }

    @Override
    protected void paintComponent(Graphics g) {
        int ancho = getWidth();
        int alto = getHeight();
        int radio = Tema.radioPequeno();
        int diametro = radio * 2;

        Graphics2D lienzo = (Graphics2D) g.create();
        lienzo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        lienzo.setColor(isFocusOwner() ? Tema.acento() : Tema.borde());
        lienzo.fillRoundRect(0, 0, ancho, alto, diametro, diametro);
        lienzo.setColor(isEnabled() ? Tema.superficie() : Tema.fondo());
        lienzo.fillRoundRect(1, 1, ancho - 2, alto - 2, diametro - 2, diametro - 2);

        if (getText().isEmpty() && !ayuda.isEmpty()) {
            var metricas = lienzo.getFontMetrics();
            int base = (alto - (metricas.getAscent() + metricas.getDescent())) / 2 + metricas.getAscent();
            lienzo.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            lienzo.setFont(getFont());
            lienzo.setColor(Tema.textoTenue());
            lienzo.drawString(ayuda, 12, base);
        }
        lienzo.dispose();
        super.paintComponent(g);
    }
}