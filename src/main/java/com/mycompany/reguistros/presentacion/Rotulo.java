package com.mycompany.reguistros.presentacion;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.util.function.Supplier;
import javax.swing.JLabel;

/**
 * Etiqueta cuyo color se resuelve en cada pintado a partir del tema activo. Así, cambiar
 * de tema no deja texto con el color del tema anterior.
 */
public class Rotulo extends JLabel {

    private final Supplier<Color> color;

    private Rotulo(String texto, Supplier<Color> color, float tamano, int estilo, boolean enVersalitas) {
        super(enVersalitas ? texto.toUpperCase() : texto, JLabel.LEFT);
        this.color = color;
        setFont(Tema.fuente(tamano, estilo));
    }

    public static Rotulo titulo(String texto) {
        return new Rotulo(texto, Tema::texto, 25f, Font.BOLD, false);
    }

    public static Rotulo subtitulo(String texto) {
        return new Rotulo(texto, Tema::textoTenue, 12.5f, Font.PLAIN, false);
    }

    /** Rotulo en versalitas de las tarjetas y de los grupos del formulario. */
    public static Rotulo seccion(String texto) {
        return new Rotulo(texto, Tema::cabeceraTexto, 11.5f, Font.BOLD, true);
    }

    public static Rotulo etiquetaDeCampo(String texto) {
        return new Rotulo(texto, Tema::textoTenue, 10.5f, Font.BOLD, true);
    }

    public static Rotulo contador(String texto) {
        return new Rotulo(texto, Tema::acento, 11.5f, Font.BOLD, false);
    }

    public static Rotulo estado(String texto) {
        return new Rotulo(texto, Tema::texto, 13f, Font.PLAIN, false);
    }

    public static Rotulo pie(String texto) {
        return new Rotulo(texto, Tema::textoTenue, 12.5f, Font.PLAIN, false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        setForeground(color.get());
        super.paintComponent(g);
    }
}