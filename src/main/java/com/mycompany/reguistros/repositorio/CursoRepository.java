package com.mycompany.reguistros.repositorio;

import com.mycompany.reguistros.dominio.Curso;
import java.util.List;
import java.util.Optional;

public interface CursoRepository {

    Curso guardar(Curso curso);

    Optional<Curso> buscarPorCodigo(String codigo);

    boolean existePorCodigo(String codigo);

    List<Curso> listarTodos();
}
