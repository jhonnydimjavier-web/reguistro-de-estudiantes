package com.mycompany.reguistros.dominio;

import com.mycompany.reguistros.dominio.excepcion.DatosInvalidosException;

public final class Codigos {

    private static final int LONGITUD_MAXIMA_CODIGO = 20;
    private static final int LONGITUD_MAXIMA_TEXTO = 100;

    private Codigos() {
    }

    public static String normalizarCodigo(String codigo, String campo) {
        if (codigo == null || codigo.isBlank()) {
            throw DatosInvalidosException.obligatorio(campo);
        }
        String normalizado = codigo.strip().toUpperCase();
        if (normalizado.length() > LONGITUD_MAXIMA_CODIGO) {
            throw DatosInvalidosException.demasiadoLargo(campo, LONGITUD_MAXIMA_CODIGO);
        }
        return normalizado;
    }

    public static String normalizarTexto(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw DatosInvalidosException.obligatorio(campo);
        }
        String normalizado = texto.strip();
        if (normalizado.length() > LONGITUD_MAXIMA_TEXTO) {
            throw DatosInvalidosException.demasiadoLargo(campo, LONGITUD_MAXIMA_TEXTO);
        }
        return normalizado;
    }
}