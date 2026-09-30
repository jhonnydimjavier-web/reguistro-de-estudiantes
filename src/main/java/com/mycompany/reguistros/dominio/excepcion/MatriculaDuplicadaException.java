package com.mycompany.reguistros.dominio.excepcion;

public class MatriculaDuplicadaException extends ReglaDeNegocioException {

    private MatriculaDuplicadaException(String mensaje) {
        super(mensaje);
    }

    public static MatriculaDuplicadaException entre(String codigoEstudiante, String codigoCurso) {
        return new MatriculaDuplicadaException(
                "El estudiante " + codigoEstudiante + " ya está matriculado en el curso " + codigoCurso);
    }
}
