package com.mycompany.reguistros.repositorio;

import com.mycompany.reguistros.dominio.Matricula;
import com.mycompany.reguistros.dominio.MatriculaId;
import java.util.List;
import java.util.Optional;

public interface MatriculaRepository {

    Matricula guardar(Matricula matricula);

    Optional<Matricula> buscarPorId(MatriculaId id);

    boolean existePorId(MatriculaId id);

    List<Matricula> listarPorEstudiante(String codigoEstudiante);

    List<Matricula> listarPorCurso(String codigoCurso);

    List<Matricula> listarTodas();

    void eliminar(MatriculaId id);
}
