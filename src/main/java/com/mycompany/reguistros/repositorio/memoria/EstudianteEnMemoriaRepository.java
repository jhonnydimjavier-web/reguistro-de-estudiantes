package com.mycompany.reguistros.repositorio.memoria;

import com.mycompany.reguistros.dominio.Codigos;
import com.mycompany.reguistros.dominio.Estudiante;
import com.mycompany.reguistros.repositorio.EstudianteRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EstudianteEnMemoriaRepository implements EstudianteRepository {

    private final Map<String, Estudiante> estudiantes = new LinkedHashMap<>();

    @Override
    public Estudiante guardar(Estudiante estudiante) {
        estudiantes.put(estudiante.getCodigo(), estudiante);
        return estudiante;
    }

    @Override
    public Optional<Estudiante> buscarPorCodigo(String codigo) {
        return Optional.ofNullable(estudiantes.get(normalizar(codigo)));
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return estudiantes.containsKey(normalizar(codigo));
    }

    @Override
    public List<Estudiante> listarTodos() {
        return List.copyOf(estudiantes.values());
    }

    private String normalizar(String codigo) {
        return Codigos.normalizarCodigo(codigo, "código de estudiante");
    }
}
