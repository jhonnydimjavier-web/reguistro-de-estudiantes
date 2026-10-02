# Sistema de Matrículas de Estudiantes

Aplicación de gestión de matrículas para una institución durante un periodo académico: registra
estudiantes y cursos, matricula y retira matrículas, y consulta las relaciones entre ambos.
Hecha en **Java 25** sin dependencias externas (JUnit 5 solo para los tests) y con una
interfaz Swing de estilo plano, con tema claro y oscuro.

## Modelo de dominio

| Entidad       | Atributos                      | Identidad                                                |
|---------------|--------------------------------|----------------------------------------------------------|
| `Estudiante`  | `codigo`, `nombre`             | `codigo` (único)                                          |
| `Curso`       | `codigo`, `nombre`, `cupo`     | `codigo` (único)                                          |
| `Matricula`   | `estudiante`, `curso`, `fecha` | compuesta `MatriculaId` (`codigoEstudiante \| codigoCurso`) |

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
8. Filtrar estudiantes, cursos y matrículas desde la ventana.

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
- **Identidad compuesta de la matrícula**: `MatriculaId` es un `record` que normaliza ambos
  códigos y define `equals`/`hashCode`; el repositorio de matrículas usa
  `Map<MatriculaId, Matricula>`.
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
  aunque cambie el nombre. Por eso `Estudiante`, `Curso` y `Matricula` son clases y no `records`:
  tienen estado mutable (`setNombre`, `setCupo`) y su igualdad no es estructural.
- **Errores de dominio cerrados**: `ReglaDeNegocioException` es `sealed` y declara sus nueve
  subtipos en `permits`, así la interfaz puede hacer un `switch` **exhaustivo** sobre el fallo
  concreto y explicar qué hacer, sin `default`. Si se añade una excepción nueva, el compilador
  avisa del `switch` que hay que actualizar.

## Lenguaje moderno usado (Java 25)

| Recurso | Dónde |
|---------|-------|
| `record` | `MatriculaId` (valor con identidad compuesta), `DemoAceptacion.EstudianteDemo`/`CursoDemo`/`MatriculaDemo` y los datos de ejemplo del formulario. |
| `sealed` + `permits` | Jerarquía `ReglaDeNegocioException`. |
| `switch` con pattern matching | `VentanaMatriculas.consejo(...)`, exhaustivo sobre la jerarquía sellada. |
| Expresiones `switch` | Menú de la consola y resolución del color de los botones y del pie. |
| Bloques de texto (`"""`) | Menú de la consola y salidas de la demo de aceptación. |
| `String.formatted` | Mensajes del dominio, de las excepciones y de la ventana. |
| Instancias de interfaz sin `public` | `Campo`, `TablaEstilo`, `Indicador`, `BarraDesplazamiento`. |

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

## La ventana

- **Tema claro y oscuro** desde el botón de la esquina superior derecha.
- **Tarjetas** con esquinas redondeadas para cada tabla y cada grupo del formulario; sin cuadrícula,
  con filas en zigzag y la fila del ratón resaltada.
- **Indicadores** arriba: estudiantes, cursos, matrículas y plazas libres, más un resumen con los
  cursos que ya están completos.
- **Búsqueda** que filtra las tres tablas a la vez, indicando `visibles / total` en cada tarjeta.
- **Un clic** en una fila de *Estudiantes* o *Cursos* deja el código en el formulario de matrícula;
  en *Matrículas* rellena ambos códigos para poder retirarla. **Doble clic** en *Matrículas* deja
  los parámetros listos para retirar.
- **Enter** confirma la acción del campo donde estés escribiendo.
- Los **errores no abren cuadros de diálogo**: aparecen en la barra de pie con un punto de color
  (rojo, verde o azul) y una recomendación según el tipo de fallo.

## Datos de ejemplo

Al abrir la ventana se cargan 8 estudiantes, 7 cursos y 15 matrículas que dejan a la vista casos
interesantes de todas las reglas: `POO` y `RED` están al 100 % de su cupo, `ALG` tiene cupo 0,
`E007` ya tiene los 3 cursos activos que permite la regla y `E008` todavía no tiene ninguna.

## Cómo compilar, probar y ejecutar

```bash
# Compilar y ejecutar los 29 tests
mvn -o test

# Ejecutar la aplicación: abre la ventana Swing
mvn -o exec:java
# o bien
java -cp target/classes com.mycompany.reguistros.Reguistros

# Alternativa en consola (demo de aceptacion + menu interactivo)
java -cp target/classes com.mycompany.reguistros.Reguistros --consola
```

Si `mvn` no está en el PATH, use el que incluye NetBeans:
`C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd`.

En NetBeans: abra el proyecto y presione **Run** (o `Shift+F6`). El divisor vertical entre las
tablas y las matrículas se puede arrastrar; la separación entre *Estudiantes* y *Cursos* es fija.

## Estructura del proyecto

```
src/main/java/com/mycompany/reguistros/
  Reguistros.java                 # punto de entrada (GUI por defecto, --consola alternativo)
  aplicacion/MatriculaService.java# casos de uso y reglas de negocio
  dominio/                        # Estudiante, Curso, Matricula, MatriculaId, Codigos
  dominio/excepcion/              # jerarquía sellada de excepciones del dominio
  repositorio/                    # puertos (interfaces) de persistencia
  repositorio/memoria/            # implementaciones en memoria con Map
  presentacion/
    VentanaMatriculas.java        # GUI: arma la ventana y conecta los eventos
    Tema.java                     # paleta clara/oscura y tipografía
    PanelRedondeado.java          # tarjeta con esquinas redondeadas
    Boton.java                    # botón plano con variantes
    CampoTexto.java               # campo con borde que se ilumina y texto de ayuda
    TablaEstilo.java              # tabla sin cuadrícula, con zigzag y fila resaltada
    Indicador.java                # tarjeta de cifra de resumen
    Rotulo.java                   # etiqueta que resuelve el color del tema al pintar
    BarraDesplazamiento.java      # barra fina sin botones
    DemoAceptacion.java           # escenario de aceptación y datos de ejemplo
    ConsolaMatriculas.java        # menú interactivo por consola

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