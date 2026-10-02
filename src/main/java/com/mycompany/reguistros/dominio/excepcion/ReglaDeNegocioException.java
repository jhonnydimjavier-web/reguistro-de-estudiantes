package com.mycompany.reguistros.dominio.excepcion;

/**
 * Raíz de los errores de reglas de negocio. Es {@code sealed}: el conjunto de fallos es
 * cerrado y conocido, así que la presentación puede hacer {@code switch} exhaustivo sobre
 * el tipo concreto de la excepción y dar una ayuda específica por cada caso.
 */
public sealed abstract class ReglaDeNegocioException extends RuntimeException
        permits DatosInvalidosException,
                EstudianteYaRegistradoException,
                EstudianteNoEncontradoException,
                CursoYaRegistradoException,
                CursoNoEncontradoException,
                CupoAgotadoException,
                MatriculaDuplicadaException,
                MatriculaNoEncontradaException,
                LimiteCursosAlcanzadoException {

    public ReglaDeNegocioException(String mensaje) {
        super(mensaje);
    }
}