package com.mycompany.reguistros.dominio.excepcion;

public final class DatosInvalidosException extends ReglaDeNegocioException {

    private DatosInvalidosException(String mensaje) {
        super(mensaje);
    }

    public static DatosInvalidosException de(String mensaje) {
        return new DatosInvalidosException(mensaje);
    }

    public static DatosInvalidosException obligatorio(String campo) {
        return new DatosInvalidosException("El %s es obligatorio".formatted(campo));
    }

    public static DatosInvalidosException demasiadoLargo(String campo, int maximo) {
        return new DatosInvalidosException("El %s no puede superar %d caracteres".formatted(campo, maximo));
    }

    public static DatosInvalidosException cupoNegativo(int cupo) {
        return new DatosInvalidosException("El cupo del curso no puede ser negativo: %d".formatted(cupo));
    }
}