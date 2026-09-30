package com.mycompany.reguistros.dominio.excepcion;

public class DatosInvalidosException extends ReglaDeNegocioException {

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
