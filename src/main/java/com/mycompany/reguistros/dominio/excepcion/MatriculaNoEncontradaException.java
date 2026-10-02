package com.mycompany.reguistros.dominio.excepcion;

public final class MatriculaNoEncontradaException extends ReglaDeNegocioException {

    private MatriculaNoEncontradaException(String mensaje) {
        super(mensaje);
    }

    public static MatriculaNoEncontradaException entre(String codigoEstudiante, String codigoCurso) {
        return new MatriculaNoEncontradaException(
                "El estudiante %s no está matriculado en el curso %s".formatted(codigoEstudiante, codigoCurso));
    }
}