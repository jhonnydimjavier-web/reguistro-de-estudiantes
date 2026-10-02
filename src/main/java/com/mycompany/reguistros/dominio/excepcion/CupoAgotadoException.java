package com.mycompany.reguistros.dominio.excepcion;

public final class CupoAgotadoException extends ReglaDeNegocioException {

    private CupoAgotadoException(String mensaje) {
        super(mensaje);
    }

    public static CupoAgotadoException en(String codigoCurso, int cupo, int matriculados) {
        return new CupoAgotadoException(
                "El curso %s está completo: %d de %d plazas ocupadas".formatted(codigoCurso, matriculados, cupo));
    }
}