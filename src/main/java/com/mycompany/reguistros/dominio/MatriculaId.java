package com.mycompany.reguistros.dominio;

import java.util.Objects;

public final class MatriculaId {

    private final String codigoEstudiante;
    private final String codigoCurso;

    public MatriculaId(String codigoEstudiante, String codigoCurso) {
        this.codigoEstudiante = Codigos.normalizarCodigo(codigoEstudiante, "código de estudiante");
        this.codigoCurso = Codigos.normalizarCodigo(codigoCurso, "código de curso");
    }

    public String getCodigoEstudiante() {
        return codigoEstudiante;
    }

    public String getCodigoCurso() {
        return codigoCurso;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        return otro instanceof MatriculaId id
                && Objects.equals(codigoEstudiante, id.codigoEstudiante)
                && Objects.equals(codigoCurso, id.codigoCurso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoEstudiante, codigoCurso);
    }

    @Override
    public String toString() {
        return codigoEstudiante + "|" + codigoCurso;
    }
}
