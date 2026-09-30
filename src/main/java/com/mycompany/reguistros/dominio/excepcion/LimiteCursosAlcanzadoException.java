package com.mycompany.reguistros.dominio.excepcion;

public class LimiteCursosAlcanzadoException extends ReglaDeNegocioException {

    private LimiteCursosAlcanzadoException(String mensaje) {
        super(mensaje);
    }

    public static LimiteCursosAlcanzadoException para(String codigoEstudiante, int limite, int cursosActivos) {
        return new LimiteCursosAlcanzadoException("El estudiante " + codigoEstudiante
                + " ya tiene " + cursosActivos + " cursos activos y el máximo permitido es " + limite);
    }
}
