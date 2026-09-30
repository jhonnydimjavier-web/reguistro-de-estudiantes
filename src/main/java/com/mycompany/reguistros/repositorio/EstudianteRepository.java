package com.mycompany.reguistros.repositorio;

import com.mycompany.reguistros.dominio.Estudiante;
import java.util.List;
import java.util.Optional;

public interface EstudianteRepository {

    Estudiante guardar(Estudiante estudiante);

    Optional<Estudiante> buscarPorCodigo(String codigo);

    boolean existePorCodigo(String codigo);

    List<Estudiante> listarTodos();
}
