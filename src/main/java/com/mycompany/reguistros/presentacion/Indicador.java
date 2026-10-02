package com.mycompany.reguistros.presentacion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * Tarjeta de indicador: una cifra grande con su etiqueta, el formato típico de un tablero
 * de resumen.
 */
public class Indicador extends PanelRedondeado {

    private final JLabel valor = new JLabel("0", SwingConstants.LEFT);
    private final Rotulo rotulo;

    public Indicador(String rotulo, String valorInicial) {
        super(new BorderLayout(0, 2));
        this.rotulo = Rotulo.seccion(rotulo);
        updateUI();
        valor.setText(valorInicial);
        add(valor, BorderLayout.NORTH);
        add(this.rotulo, BorderLayout.SOUTH);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (valor == null) {
            return;
        }
        valor.setFont(Tema.fuente(26f, Font.BOLD));
        valor.setForeground(Tema.texto());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, Tema.acento()),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
    }

    public void setValor(String texto, Color color) {
        valor.setText(texto);
        valor.setForeground(color);
    }
}