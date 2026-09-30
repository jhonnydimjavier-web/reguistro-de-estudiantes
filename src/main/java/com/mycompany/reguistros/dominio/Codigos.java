package com.mycompany.reguistros.dominio;

import com.mycompany.reguistros.dominio.excepcion.DatosInvalidosException;

public final class Codigos {

    private static final int LONGITUD_MAXIMA_CODIGO = 20;
    private static final int LONGITUD_MAXIMA_TEXTO = 100;

    private Codigos() {
    }

    public static String normalizarCodigo(String codigo, String campo) {
        if (codigo == null || codigo.isBlank()) {
            throw new DatosInvalidosException("El " + campo + " es obligatorio");
        }
        String normalizado = codigo.strip().toUpperCase();
        if (normalizado.length() > LONGITUD_MAXIMA_CODIGO) {
            throw new DatosInvalidosException(
                    "El " + campo + " no puede superar " + LONGITUD_MAXIMA_CODIGO + " caracteres");
        }
        return normalizado;
    }

    public static String normalizarTexto(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new DatosInvalidosException("El " + campo + " es obligatorio");
        }
        String normalizado = texto.strip();
        if (normalizado.length() > LONGITUD_MAXIMA_TEXTO) {
            throw new DatosInvalidosException(
                    "El " + campo + " no puede superar " + LONGITUD_MAXIMA_TEXTO + " caracteres");
        }
        return normalizado;
    }
}
