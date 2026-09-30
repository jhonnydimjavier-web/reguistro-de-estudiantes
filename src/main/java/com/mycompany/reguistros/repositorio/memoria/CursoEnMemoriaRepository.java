package com.mycompany.reguistros.repositorio.memoria;

import com.mycompany.reguistros.dominio.Codigos;
import com.mycompany.reguistros.dominio.Curso;
import com.mycompany.reguistros.repositorio.CursoRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CursoEnMemoriaRepository implements CursoRepository {

    private final Map<String, Curso> cursos = new LinkedHashMap<>();

    @Override
    public Curso guardar(Curso curso) {
        cursos.put(curso.getCodigo(), curso);
        return curso;
    }

    @Override
    public Optional<Curso> buscarPorCodigo(String codigo) {
        return Optional.ofNullable(cursos.get(normalizar(codigo)));
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return cursos.containsKey(normalizar(codigo));
    }

    @Override
    public List<Curso> listarTodos() {
        return List.copyOf(cursos.values());
    }

    private String normalizar(String codigo) {
        return Codigos.normalizarCodigo(codigo, "código de curso");
    }
}
