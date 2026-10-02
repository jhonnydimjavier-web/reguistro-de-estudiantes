package com.mycompany.reguistros.dominio.excepcion;

public final class EstudianteYaRegistradoException extends ReglaDeNegocioException {

    private EstudianteYaRegistradoException(String mensaje) {
        super(mensaje);
    }

    public static EstudianteYaRegistradoException de(String codigo) {
        return new EstudianteYaRegistradoException("Ya existe un estudiante con el código %s".formatted(codigo));
    }
}