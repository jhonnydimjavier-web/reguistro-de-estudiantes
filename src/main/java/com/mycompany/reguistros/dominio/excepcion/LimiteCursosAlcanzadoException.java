package com.mycompany.reguistros.dominio.excepcion;

public final class LimiteCursosAlcanzadoException extends ReglaDeNegocioException {

    private LimiteCursosAlcanzadoException(String mensaje) {
        super(mensaje);
    }

    public static LimiteCursosAlcanzadoException para(String codigoEstudiante, int limite, int cursosActivos) {
        return new LimiteCursosAlcanzadoException(
                "El estudiante %s ya tiene %d cursos activos y el máximo permitido es %d"
                        .formatted(codigoEstudiante, cursosActivos, limite));
    }
}