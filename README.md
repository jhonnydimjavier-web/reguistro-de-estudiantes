# Sistema de Matrículas de Estudiantes

Aplicación de gestión de matrículas para una institución durante un periodo académico: registra
estudiantes y cursos, matricula y retira matrículas, y consulta las relaciones entre ambos.

## Modelo de dominio

| Entidad            | Atributos                    | Identidad                    |
|--------------------|------------------------------|------------------------------|
| `Estudiante`       | `codigo`, `nombre`            | `codigo` (único)             |
| `Curso`            | `codigo`, `nombre`, `cupo`   | `codigo` (único)             |
| `Matricula`        | `estudiante`, `curso`, `fecha` | compuesta `MatriculaId` (`codigoEstudiante \| codigoCurso`) |

La matrícula no tiene código propio: su identidad es la pareja (`codigoEstudiante`, `codigoCurso`),
por eso se modela como una **relación con identidad compuesta**.

## Casos de uso implementados

1. Registrar estudiante y curso.
2. Buscar estudiante y curso por código.
3. Matricular a un estudiante en un curso (con fecha del periodo en curso).
4. Retirar (cancelar) una matrícula; la plaza queda libre.
5. Listar los cursos de un estudiante.
6. Listar los estudiantes de un curso.
7. Listar todos los estudiantes y todos los cursos.
8. (GUI) Ver estudiantes, cursos y matrículas como tablas en una ventana Swing.

## Contrato de `MatriculaService`

| Método | Descripción |
|--------|-------------|
| `Estudiante registrarEstudiante(String codigo, String nombre)` | Registra un estudiante. |
| `Curso registrarCurso(String codigo, String nombre, int cupo)` | Registra un curso. |
| `Optional<Estudiante> buscarEstudiante(String codigo)` | Busca por código normalizado. |
| `Optional<Curso> buscarCurso(String codigo)` | Busca por código normalizado. |
| `Matricula matricular(String codigoEstudiante, String codigoCurso)` | Matricula; aplica todas las reglas de negocio. |
| `Matricula retirar(String codigoEstudiante, String codigoCurso)` | Retira la matrícula y la devuelve. |
| `List<Curso> listarCursos(String codigoEstudiante)` | Cursos en los que está matriculado el estudiante. |
| `List<Estudiante> listarEstudiantes(String codigoCurso)` | Estudiantes matriculados en el curso. |
| `List<Estudiante> listarEstudiantes()` | Todos los estudiantes (orden de registro). |
| `List<Curso> listarCursos()` | Todos los cursos (orden de registro). |
| `List<Matricula> listarMatriculas()` | Todas las matrículas (orden de registro). |

## Decisiones de diseño (para la defensa)

- **Repositorios en memoria por código**: `Map<String, Estudiante>` y `Map<String, Curso>` con
  `LinkedHashMap`, para conservar el orden de ingreso y que las consultas sean deterministas.
- **Identidad compuesta de la matrícula**: `MatriculaId` como objeto de valor que normaliza ambos
  códigos y define `equals`/`hashCode`; el repositorio de matrículas usa `Map<MatriculaId, Matricula>`.
- **Normalización de códigos** (`Codigos`): los códigos se recortan y convierten a mayúsculas en el
  límite del dominio, así `e001` y `E001` refieren a la misma entidad y la identidad compuesta es fiable.
- **Reglas de negocio en el servicio de aplicación**, no en los repositorios: los repositorios solo
  persisten; el servicio valida y decide (una sola fuente de verdad para las reglas).
- **Reloj inyectado (`java.time.Clock`)**: la fecha de la matrícula es determinista y testeable.
- **Precedencia de validaciones en `matricular`**:
  1. duplicado (el estudiante ya está en el curso),
  2. cupo del curso,
  3. máximo de cursos activos del estudiante.
  El duplicado se valida primero porque un estudiante ya matriculado en un curso lleno debe recibir
  "matrícula duplicada", no "cupo agotado".
- **Entidades con igualdad por identidad**: dos estudiantes con el mismo código son la misma entidad,
  aunque cambie el nombre.

## Reglas de negocio

1. Un estudiante **no puede** matricularse dos veces en el mismo curso.
2. Nunca se supera el **cupo** del curso.
3. **Retirar** una matrícula libera la plaza.
4. **(Cambio para la defensa)** Un estudiante no puede tener más de
   `MAX_CURSOS_ACTIVOS_POR_ESTUDIANTE = 3` cursos activos a la vez (`MatriculaService.java`).

Las violaciones lanzan excepciones del dominio bajo la raíz `ReglaDeNegocioException`
(`MatriculaDuplicadaException`, `CupoAgotadoException`, `MatriculaNoEncontradaException`,
`LimiteCursosAlcanzadoException`, etc.), siempre **sin cambiar el estado** del sistema.

## Escenario de aceptación

1. Se crea el curso `POO` con cupo **2**.
2. Se matriculan `E001` y `E002` → ambos ocupan las 2 plazas.
3. Se rechaza a `E003` (cupo agotado) y se rechaza la matrícula repetida de `E001` (duplicada).
4. `E001` retira su matrícula → queda 1 plaza libre.
5. `E003` se matricula → el cupo vuelve a estar completo (2 de 2).

Este escenario se reproduce automáticamente al ejecutar el programa y está cubierto por tests.

## Casos límite probados

- Cupo agotado (incluido curso con cupo 0).
- Matrícula duplicada (no crea una segunda relación).
- Retiro inexistente (sin cambios de estado).
- Retiro repetido de la misma matrícula (solo surte efecto la primera vez).
- Máximo de 3 cursos activos por estudiante, el límite es por estudiante y el retiro lo libera.
- Códigos duplicados al registrar y datos obligatorios vacíos.
- Búsqueda de entidades inexistentes.

## Cómo compilar, probar y ejecutar

```bash
# Compilar y ejecutar los 29 tests
mvn -o test

# Ejecutar la aplicación: abre la ventana Swing con las tablas de datos
mvn -o exec:java
# o bien
java -cp target/classes com.mycompany.reguistros.Reguistros

# Alternativa en consola (demo de aceptacion + menu interactivo)
java -cp target/classes com.mycompany.reguistros.Reguistros --consola
```

Si `mvn` no está en el PATH, use el que incluye NetBeans:
`C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd`.

En NetBeans: abra el proyecto y presione **Run** (o `Shift+F6`) para ver la ventana con las
tres tablas (estudiantes, cursos, matrículas); los botones permiten registrar, matricular y
retirar, y las tablas se refrescan tras cada operación. Los errores de reglas se muestran en
cuadros de diálogo.

## Estructura del proyecto

```
src/main/java/com/mycompany/reguistros/
  Reguistros.java                 # punto de entrada (GUI por defecto, --consola alternativo)
  aplicacion/MatriculaService.java# casos de uso y reglas de negocio
  dominio/                        # Estudiante, Curso, Matricula, MatriculaId, Codigos
  dominio/excepcion/              # jerarquia de excepciones del dominio
  repositorio/                    # puertos (interfaces) de persistencia
  repositorio/memoria/            # implementaciones en memoria con Map
  presentacion/                   # VentanaMatriculas (GUI), DemoAceptacion, ConsolaMatriculas

src/test/java/...                 # tests (JUnit 5)
```

## Cobertura de tests (JUnit 5)

| Clase de test | Tests |
|---|---|
| `EscenarioDeAceptacionTest` | 5 |
| `RegistroYBusquedaTest` | 7 |
| `CasosLimiteTest` | 10 |
| `DominioTest` | 7 |
| **Total** | **29** |