package com.mycompany.reguistros.dominio;

import com.mycompany.reguistros.dominio.excepcion.DatosInvalidosException;
import java.util.Objects;

public class Curso {

    private final String codigo;
    private String nombre;
    private int cupo;

    public Curso(String codigo, String nombre, int cupo) {
        this.codigo = Codigos.normalizarCodigo(codigo, "código de curso");
        this.nombre = Codigos.normalizarTexto(nombre, "nombre del curso");
        if (cupo < 0) {
            throw new DatosInvalidosException("El cupo del curso no puede ser negativo: " + cupo);
        }
        this.cupo = cupo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCupo() {
        return cupo;
    }

    public void setNombre(String nombre) {
        this.nombre = Codigos.normalizarTexto(nombre, "nombre del curso");
    }

    public void setCupo(int cupo) {
        if (cupo < 0) {
            throw new DatosInvalidosException("El cupo del curso no puede ser negativo: " + cupo);
        }
        this.cupo = cupo;
    }

    public boolean tieneCupoDisponible(int matriculados) {
        return matriculados < cupo;
    }

    public int plazasDisponibles(int matriculados) {
        return Math.max(0, cupo - matriculados);
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        return otro instanceof Curso curso && Objects.equals(codigo, curso.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (cupo " + cupo + ")";
    }
}
