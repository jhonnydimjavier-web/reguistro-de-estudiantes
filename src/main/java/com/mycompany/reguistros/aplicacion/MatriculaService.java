package com.mycompany.reguistros.aplicacion;

import com.mycompany.reguistros.dominio.Codigos;
import com.mycompany.reguistros.dominio.Curso;
import com.mycompany.reguistros.dominio.Estudiante;
import com.mycompany.reguistros.dominio.Matricula;
import com.mycompany.reguistros.dominio.MatriculaId;
import com.mycompany.reguistros.dominio.excepcion.CursoNoEncontradoException;
import com.mycompany.reguistros.dominio.excepcion.CursoYaRegistradoException;
import com.mycompany.reguistros.dominio.excepcion.CupoAgotadoException;
import com.mycompany.reguistros.dominio.excepcion.EstudianteNoEncontradoException;
import com.mycompany.reguistros.dominio.excepcion.EstudianteYaRegistradoException;
import com.mycompany.reguistros.dominio.excepcion.LimiteCursosAlcanzadoException;
import com.mycompany.reguistros.dominio.excepcion.MatriculaDuplicadaException;
import com.mycompany.reguistros.dominio.excepcion.MatriculaNoEncontradaException;
import com.mycompany.reguistros.repositorio.CursoRepository;
import com.mycompany.reguistros.repositorio.EstudianteRepository;
import com.mycompany.reguistros.repositorio.MatriculaRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MatriculaService {

    public static final int MAX_CURSOS_ACTIVOS_POR_ESTUDIANTE = 3;

    private final EstudianteRepository estudiantes;
    private final CursoRepository cursos;
    private final MatriculaRepository matriculas;
    private final Clock reloj;

    public MatriculaService(EstudianteRepository estudiantes, CursoRepository cursos,
            MatriculaRepository matriculas) {
        this(estudiantes, cursos, matriculas, Clock.systemDefaultZone());
    }

    public MatriculaService(EstudianteRepository estudiantes, CursoRepository cursos,
            MatriculaRepository matriculas, Clock reloj) {
        this.estudiantes = estudiantes;
        this.cursos = cursos;
        this.matriculas = matriculas;
        this.reloj = reloj;
    }

    public Estudiante registrarEstudiante(String codigo, String nombre) {
        String clave = Codigos.normalizarCodigo(codigo, "código de estudiante");
        if (estudiantes.existePorCodigo(clave)) {
            throw EstudianteYaRegistradoException.de(clave);
        }
        return estudiantes.guardar(new Estudiante(clave, nombre));
    }

    public Curso registrarCurso(String codigo, String nombre, int cupo) {
        String clave = Codigos.normalizarCodigo(codigo, "código de curso");
        if (cursos.existePorCodigo(clave)) {
            throw CursoYaRegistradoException.de(clave);
        }
        return cursos.guardar(new Curso(clave, nombre, cupo));
    }

    public Optional<Estudiante> buscarEstudiante(String codigo) {
        return estudiantes.buscarPorCodigo(codigo);
    }

    public Optional<Curso> buscarCurso(String codigo) {
        return cursos.buscarPorCodigo(codigo);
    }

    public List<Estudiante> listarEstudiantes() {
        return estudiantes.listarTodos();
    }

    public List<Curso> listarCursos() {
        return cursos.listarTodos();
    }

    public List<Matricula> listarMatriculas() {
        return matriculas.listarTodas();
    }

    public Matricula matricular(String codigoEstudiante, String codigoCurso) {
        Estudiante estudiante = obtenerEstudiante(codigoEstudiante);
        Curso curso = obtenerCurso(codigoCurso);
        MatriculaId id = new MatriculaId(estudiante.getCodigo(), curso.getCodigo());

        if (matriculas.existePorId(id)) {
            throw MatriculaDuplicadaException.entre(id.getCodigoEstudiante(), id.getCodigoCurso());
        }

        int matriculados = matriculas.listarPorCurso(curso.getCodigo()).size();
        if (!curso.tieneCupoDisponible(matriculados)) {
            throw CupoAgotadoException.en(curso.getCodigo(), curso.getCupo(), matriculados);
        }

        int cursosActivos = matriculas.listarPorEstudiante(estudiante.getCodigo()).size();
        if (cursosActivos >= MAX_CURSOS_ACTIVOS_POR_ESTUDIANTE) {
            throw LimiteCursosAlcanzadoException.para(
                    estudiante.getCodigo(), MAX_CURSOS_ACTIVOS_POR_ESTUDIANTE, cursosActivos);
        }

        return matriculas.guardar(new Matricula(estudiante, curso, LocalDate.now(reloj)));
    }

    public Matricula retirar(String codigoEstudiante, String codigoCurso) {
        MatriculaId id = new MatriculaId(codigoEstudiante, codigoCurso);
        Matricula matricula = matriculas.buscarPorId(id)
                .orElseThrow(() -> MatriculaNoEncontradaException.entre(
                        id.getCodigoEstudiante(), id.getCodigoCurso()));
        matriculas.eliminar(id);
        return matricula;
    }

    public List<Curso> listarCursos(String codigoEstudiante) {
        Estudiante estudiante = obtenerEstudiante(codigoEstudiante);
        return matriculas.listarPorEstudiante(estudiante.getCodigo()).stream()
                .map(Matricula::getCurso)
                .toList();
    }

    public List<Estudiante> listarEstudiantes(String codigoCurso) {
        Curso curso = obtenerCurso(codigoCurso);
        return matriculas.listarPorCurso(curso.getCodigo()).stream()
                .map(Matricula::getEstudiante)
                .toList();
    }

    private Estudiante obtenerEstudiante(String codigo) {
        String clave = Codigos.normalizarCodigo(codigo, "código de estudiante");
        return estudiantes.buscarPorCodigo(clave)
                .orElseThrow(() -> EstudianteNoEncontradoException.de(clave));
    }

    private Curso obtenerCurso(String codigo) {
        String clave = Codigos.normalizarCodigo(codigo, "código de curso");
        return cursos.buscarPorCodigo(clave)
                .orElseThrow(() -> CursoNoEncontradoException.de(clave));
    }
}
