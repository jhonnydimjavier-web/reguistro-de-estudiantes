package com.mycompany.reguistros.dominio.excepcion;

public class MatriculaNoEncontradaException extends ReglaDeNegocioException {

    private MatriculaNoEncontradaException(String mensaje) {
        super(mensaje);
    }

    public static MatriculaNoEncontradaException entre(String codigoEstudiante, String codigoCurso) {
        return new MatriculaNoEncontradaException(
                "El estudiante " + codigoEstudiante + " no está matriculado en el curso " + codigoCurso);
    }
}
