package com.mycompany.reguistros.dominio.excepcion;

public class CupoAgotadoException extends ReglaDeNegocioException {

    private CupoAgotadoException(String mensaje) {
        super(mensaje);
    }

    public static CupoAgotadoException en(String codigoCurso, int cupo, int matriculados) {
        return new CupoAgotadoException("El curso " + codigoCurso + " no tiene plazas disponibles: cupo "
                + matriculados + " de " + cupo);
    }
}
