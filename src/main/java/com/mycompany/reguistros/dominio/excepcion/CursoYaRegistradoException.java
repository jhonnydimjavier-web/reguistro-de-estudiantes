package com.mycompany.reguistros.dominio.excepcion;

public class CursoYaRegistradoException extends ReglaDeNegocioException {

    private CursoYaRegistradoException(String mensaje) {
        super(mensaje);
    }

    public static CursoYaRegistradoException de(String codigo) {
        return new CursoYaRegistradoException("Ya existe un curso con el código " + codigo);
    }
}
