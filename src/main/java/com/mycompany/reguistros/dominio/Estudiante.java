package com.mycompany.reguistros.dominio;

import java.util.Objects;

public class Estudiante {

    private final String codigo;
    private String nombre;

    public Estudiante(String codigo, String nombre) {
        this.codigo = Codigos.normalizarCodigo(codigo, "código de estudiante");
        this.nombre = Codigos.normalizarTexto(nombre, "nombre del estudiante");
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = Codigos.normalizarTexto(nombre, "nombre del estudiante");
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        return otro instanceof Estudiante estudiante && Objects.equals(codigo, estudiante.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
