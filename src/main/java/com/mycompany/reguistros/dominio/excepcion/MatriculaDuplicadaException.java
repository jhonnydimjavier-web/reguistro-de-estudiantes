package com.mycompany.reguistros.dominio.excepcion;

public final class MatriculaDuplicadaException extends ReglaDeNegocioException {

    private MatriculaDuplicadaException(String mensaje) {
        super(mensaje);
    }

    public static MatriculaDuplicadaException entre(String codigoEstudiante, String codigoCurso) {
        return new MatriculaDuplicadaException(
                "El estudiante %s ya está matriculado en el curso %s".formatted(codigoEstudiante, codigoCurso));
    }
}