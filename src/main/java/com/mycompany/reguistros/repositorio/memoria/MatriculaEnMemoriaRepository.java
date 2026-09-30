package com.mycompany.reguistros.repositorio.memoria;

import com.mycompany.reguistros.dominio.Codigos;
import com.mycompany.reguistros.dominio.Matricula;
import com.mycompany.reguistros.dominio.MatriculaId;
import com.mycompany.reguistros.repositorio.MatriculaRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MatriculaEnMemoriaRepository implements MatriculaRepository {

    private final Map<MatriculaId, Matricula> matriculas = new LinkedHashMap<>();

    @Override
    public Matricula guardar(Matricula matricula) {
        matriculas.put(matricula.getId(), matricula);
        return matricula;
    }

    @Override
    public Optional<Matricula> buscarPorId(MatriculaId id) {
        return Optional.ofNullable(matriculas.get(id));
    }

    @Override
    public boolean existePorId(MatriculaId id) {
        return matriculas.containsKey(id);
    }

    @Override
    public List<Matricula> listarPorEstudiante(String codigoEstudiante) {
        String codigo = Codigos.normalizarCodigo(codigoEstudiante, "código de estudiante");
        return matriculas.values().stream()
                .filter(matricula -> matricula.getId().getCodigoEstudiante().equals(codigo))
                .toList();
    }

    @Override
    public List<Matricula> listarPorCurso(String codigoCurso) {
        String codigo = Codigos.normalizarCodigo(codigoCurso, "código de curso");
        return matriculas.values().stream()
                .filter(matricula -> matricula.getId().getCodigoCurso().equals(codigo))
                .toList();
    }

    @Override
    public List<Matricula> listarTodas() {
        return List.copyOf(matriculas.values());
    }

    @Override
    public void eliminar(MatriculaId id) {
        matriculas.remove(id);
    }
}
