package com.mycompany.reguistros.dominio;

public record MatriculaId(String codigoEstudiante, String codigoCurso) {

    public MatriculaId {
        codigoEstudiante = Codigos.normalizarCodigo(codigoEstudiante, "código de estudiante");
        codigoCurso = Codigos.normalizarCodigo(codigoCurso, "código de curso");
    }

    @Override
    public String toString() {
        return codigoEstudiante + "|" + codigoCurso;
    }
}