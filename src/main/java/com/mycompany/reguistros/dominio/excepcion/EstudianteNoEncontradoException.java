package com.mycompany.reguistros.dominio.excepcion;

public class EstudianteNoEncontradoException extends ReglaDeNegocioException {

    private EstudianteNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public static EstudianteNoEncontradoException de(String codigo) {
        return new EstudianteNoEncontradoException("No existe un estudiante con el código " + codigo);
    }
}
