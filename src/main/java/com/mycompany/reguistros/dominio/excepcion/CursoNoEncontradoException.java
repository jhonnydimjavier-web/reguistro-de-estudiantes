package com.mycompany.reguistros.dominio.excepcion;

public class CursoNoEncontradoException extends ReglaDeNegocioException {

    private CursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public static CursoNoEncontradoException de(String codigo) {
        return new CursoNoEncontradoException("No existe un curso con el código " + codigo);
    }
}
