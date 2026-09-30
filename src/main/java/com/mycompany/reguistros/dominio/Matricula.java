package com.mycompany.reguistros.dominio;

import com.mycompany.reguistros.dominio.excepcion.DatosInvalidosException;
import java.time.LocalDate;
import java.util.Objects;

public class Matricula {

    private final MatriculaId id;
    private final Estudiante estudiante;
    private final Curso curso;
    private final LocalDate fecha;

    public Matricula(Estudiante estudiante, Curso curso, LocalDate fecha) {
        if (estudiante == null) {
            throw new DatosInvalidosException("La matrícula requiere un estudiante");
        }
        if (curso == null) {
            throw new DatosInvalidosException("La matrícula requiere un curso");
        }
        if (fecha == null) {
            throw new DatosInvalidosException("La matrícula requiere una fecha");
        }
        this.estudiante = estudiante;
        this.curso = curso;
        this.fecha = fecha;
        this.id = new MatriculaId(estudiante.getCodigo(), curso.getCodigo());
    }

    public MatriculaId getId() {
        return id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Curso getCurso() {
        return curso;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        return otro instanceof Matricula matricula && Objects.equals(id, matricula.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + " (" + fecha + ")";
    }
}
