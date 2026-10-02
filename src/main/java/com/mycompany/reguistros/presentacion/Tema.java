package com.mycompany.reguistros.presentacion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Paleta y tipografía de la aplicación. Todos los componentes consultan el tema activo en
 * cada pintado, así que cambiar de tema solo exige repintar: no hay colores cacheados.
 */
public enum Tema {

    CLARO(new Paleta(
            new Color(0xF3F5F9), new Color(0xFFFFFF), new Color(0xE3E8EF),
            new Color(0x0F172A), new Color(0x64748B),
            new Color(0x2563EB), new Color(0x1D4ED8), new Color(0xE8EFFE),
            new Color(0xDC2626), new Color(0xFEECEC),
            new Color(0x047857), new Color(0xE4F6EF),
            new Color(0xF8FAFC), new Color(0x4A5568),
            new Color(0xFAFBFD), new Color(0xD8E3FB), new Color(0x0F172A), new Color(15, 23, 42, 28))),

    OSCURO(new Paleta(
            new Color(0x0B1020), new Color(0x141C2E), new Color(0x27324A),
            new Color(0xE7EDF7), new Color(0x94A3B8),
            new Color(0x4C8DFF), new Color(0x6FA5FF), new Color(0x1B2C4D),
            new Color(0xF87171), new Color(0x3B1418),
            new Color(0x34D399), new Color(0x0E3329),
            new Color(0x1A2438), new Color(0xA7B4CA),
            new Color(0x171F31), new Color(0x24406E), new Color(0xF2F7FF), new Color(0, 0, 0, 70)));

    record Paleta(Color fondo, Color superficie, Color borde,
                  Color texto, Color textoTenue,
                  Color acento, Color acentoHover, Color acentoTenue,
                  Color peligro, Color peligroTenue,
                  Color exito, Color exitoTenue,
                  Color cabecera, Color cabeceraTexto,
                  Color filaPar, Color seleccion, Color seleccionTexto, Color sombra) {
    }

    private static final Map<Tema, Color> TONOS = new EnumMap<>(Tema.class);
    private static final int RADIO = 14;

    static {
        TONOS.put(CLARO, new Color(0x0F172A));
        TONOS.put(OSCURO, new Color(0xF8FAFC));
    }

    private static Tema activo = CLARO;

    private final Paleta paleta;

    Tema(Paleta paleta) {
        this.paleta = paleta;
    }

    /** Aplica el tema y repinta todo lo que ya está abierto. */
    public static void activar(Tema tema) {
        if (tema == activo) {
            return;
        }
        activo = tema;
        UIManager.put("ToolTip.background", paleta().superficie());
        UIManager.put("ToolTip.foreground", paleta().texto());
        UIManager.put("ToolTip.border", javax.swing.BorderFactory.createLineBorder(paleta().borde()));
        for (Window ventana : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(ventana);
            ventana.repaint();
        }
    }

    public static Tema activo() {
        return activo;
    }

    /** Deja la aplicación en un estado conocido antes de construir la ventana. */
    public static void preparar() {
        try {
            for (UIManager.LookAndFeelInfo apariencia : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(apariencia.getName())) {
                    UIManager.setLookAndFeel(apariencia.getClassName());
                    break;
                }
            }
        } catch (Exception ignorada) {
            // se sigue con el aspecto por defecto del sistema
        }
        UIManager.put("Table.showHorizontalLines", Boolean.FALSE);
        UIManager.put("Table.showVerticalLines", Boolean.FALSE);
        UIManager.put("TextField.caretBlinkRate", 500);
    }

    public static int radio() {
        return RADIO;
    }

    public static int radioPequeno() {
        return RADIO - 6;
    }

    public static Color fondo() {
        return paleta().fondo();
    }

    public static Color superficie() {
        return paleta().superficie();
    }

    public static Color borde() {
        return paleta().borde();
    }

    public static Color texto() {
        return paleta().texto();
    }

    public static Color textoTenue() {
        return paleta().textoTenue();
    }

    public static Color acento() {
        return paleta().acento();
    }

    public static Color acentoHover() {
        return paleta().acentoHover();
    }

    public static Color acentoTenue() {
        return paleta().acentoTenue();
    }

    public static Color peligro() {
        return paleta().peligro();
    }

    public static Color peligroTenue() {
        return paleta().peligroTenue();
    }

    public static Color exito() {
        return paleta().exito();
    }

    public static Color exitoTenue() {
        return paleta().exitoTenue();
    }

    public static Color cabecera() {
        return paleta().cabecera();
    }

    public static Color cabeceraTexto() {
        return paleta().cabeceraTexto();
    }

    public static Color filaPar() {
        return paleta().filaPar();
    }

    public static Color seleccion() {
        return paleta().seleccion();
    }

    public static Color seleccionTexto() {
        return paleta().seleccionTexto();
    }

    public static Color sombra() {
        return paleta().sombra();
    }

    public static Color tono() {
        return TONOS.get(activo);
    }

    public static Font fuente(float tamano, int estilo) {
        Font base = UIManager.getFont("Label.font");
        String familia = base == null ? Font.SANS_SERIF : base.getFamily();
        return new Font(familia, estilo, Math.round(tamano));
    }

    public static Font fuente(float tamano) {
        return fuente(tamano, Font.PLAIN);
    }

    private static Paleta paleta() {
        return activo.paleta;
    }
}