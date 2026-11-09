# Guía del proyecto para preparar un guion de vídeo

Este documento reúne el contexto técnico y las evidencias del proyecto UDITflix. Se puede compartir con Claude para que proponga un guion en español, pero el alumno debe revisar el resultado y explicar el código con sus propias palabras.

## Ficha rápida

- **Proyecto:** Reto 3, Prueba B: Optimizador de descarga masiva.
- **Asignatura:** Programación de Servicios y Procesos, 2.º DAM.
- **Lenguaje:** Java.
- **Objetivo:** simular la descarga concurrente de cuatro módulos y habilitar el login solo cuando hayan terminado todos.
- **Público del vídeo:** docente y estudiantes que estén aprendiendo `Thread` y concurrencia.
- **Duración que pide el reto para la defensa técnica:** menos de 3 minutos.
- **IDE recomendado para la grabación:** IntelliJ IDEA, con la consola legible.

## El problema que resuelve

UDITflix necesita varios módulos para completar la precarga previa al login: datos del catálogo, sesión y perfil, portadas y analítica. Si se descargaran uno tras otro, los tiempos de espera se acumularían. El programa simula que las cuatro tareas trabajan en paralelo, registra cuándo empieza y termina cada una, y muestra «LOGIN HABILITADO» solo después de comprobar que todas han terminado.

No es un cliente de red real: `Thread.sleep()` simula la latencia. No hay peticiones HTTP, servidores, base de datos ni interfaz gráfica.

## Archivos importantes

- [MainUDITflix.java](Demo/src/main/java/org/example/udit/flix/MainUDITflix.java): configura las cuatro descargas, crea y prioriza sus hilos, los inicia, los espera y calcula el resumen.
- [TareaDescarga.java](Demo/src/main/java/org/example/udit/flix/TareaDescarga.java): trabajo que ejecuta cada hilo; imprime su latencia y prioridad, simula la espera y anuncia la finalización.
- [pom.xml](Demo/pom.xml): proyecto Maven, configurado con `source` y `target` Java 17.
- [README.md](README.md): documentación de la entrega, tres mediciones reales y respuestas técnicas.

## Las cuatro descargas y prioridades

| Nombre del hilo | Módulo | Prioridad |
|---|---|---:|
| `DL-catalogo` | `catalogo-metadatos` | `Thread.MAX_PRIORITY` = 10 |
| `DL-sesion` | `sesion-y-perfil` | `Thread.MAX_PRIORITY` = 10 |
| `DL-portadas` | `portadas-assets` | `Thread.NORM_PRIORITY` = 5 |
| `DL-analitica` | `analiticas` | `Thread.MIN_PRIORITY` = 1 |

Cada latencia se genera con `1000 + azar.nextInt(3001)`, así que es un valor entre 1000 y 4000 ms, inclusive. La prioridad indica una preferencia al planificador de la JVM y del sistema operativo; no promete que ese hilo termine primero.

## Recorrido del programa

1. `main` crea un `Random`, una `ArrayList<Thread>` y una lista de tareas.
2. Llama a `agregarModulo(...)` una vez por cada módulo. El método genera su latencia, crea `TareaDescarga`, construye el hilo con nombre y prioridad, y añade tanto la tarea como el hilo a sus listas.
3. Recorre la lista de hilos con un primer bucle y ejecuta `start()` para cada uno. Esto permite que todos empiecen antes de esperar por alguno.
4. Recorre los hilos con un segundo bucle y ejecuta `join()`. `main` queda esperando hasta que cada uno haya terminado.
5. Calcula la latencia mayor y la suma de las cuatro latencias.
6. Imprime el mensaje de login habilitado y las estadísticas. El login no se anuncia antes de los `join()`.

Los dos bucles separados son importantes. Si se hiciera `start()` seguido inmediatamente de `join()` dentro de un único bucle, cada descarga se esperaría antes de iniciar la siguiente, perdiendo la concurrencia efectiva.

## Conceptos que debería explicar el guion

- **`Runnable`:** describe el trabajo que hay que realizar; en este caso, una descarga simulada.
- **`Thread`:** representa el hilo que ejecutará ese trabajo.
- **`start()`:** inicia un hilo nuevo. La JVM ejecuta el `run()` asociado de forma concurrente con `main` y con los demás hilos.
- **`run()`:** contiene el trabajo. Invocarlo directamente no crea un hilo nuevo; ejecutaría el código en el hilo que hizo la llamada.
- **`Thread.sleep(ms)`:** pausa temporalmente ese hilo y simula la latencia. No es una descarga real ni significa que el hilo esté usando activamente la CPU.
- **`setPriority()` / `getPriority()`:** configura e imprime la prioridad; no es una garantía de orden.
- **`join()`:** permite que `main` espere a un hilo concreto. Al hacer `join()` a todos, el resumen se imprime después de que finalicen todas las tareas.
- **`ArrayList<Thread>`:** permite guardar y gestionar los hilos con bucles reutilizables; para añadir otro módulo basta con agregar otra llamada a `agregarModulo(...)`.
- **Concurrencia y tiempo:** como las cuatro esperas se solapan, el tiempo total tiende a la latencia mayor (más la sobrecarga de ejecución), no a la suma de las latencias.

## Evidencia de tres ejecuciones

El orden de latencias de la tabla es catálogo / sesión / portadas / analítica. Son tres ejecuciones observadas en Windows 11 Home, Java 25.0.1 y 12 procesadores lógicos.

| Ejecución | Latencias (ms) | Orden de finalización | Tiempo real | Mayor latencia | Suma |
|---:|---|---|---:|---:|---:|
| 1 | 1382 / 1551 / 1498 / 2206 | catálogo → portadas → sesión → analítica | 2217 ms | 2206 ms | 6637 ms |
| 2 | 1483 / 3577 / 2539 / 1721 | catálogo → analítica → portadas → sesión | 3593 ms | 3577 ms | 9320 ms |
| 3 | 2283 / 2515 / 3440 / 2630 | catálogo → sesión → analítica → portadas | 3455 ms | 3440 ms | 10868 ms |

**Conclusión defendible:** en estas tres muestras el tiempo real fue cercano a la latencia mayor y mucho menor que la suma. El orden cambió y no hubo una ventaja consistente para los hilos prioritarios. En la primera ejecución la analítica, prioridad mínima, terminó última; en la segunda la sesión, prioridad máxima, fue la última. Las tareas pasan la mayor parte del tiempo en `sleep()`, por lo que la prueba no mide bien prioridades bajo competencia de CPU.

No presentar estos datos como universales: las latencias son aleatorias y el resultado puede cambiar en cada ejecución y en otro equipo.

## Ejemplo de salida real

La planificación puede hacer que los mensajes de inicio aparezcan en un orden distinto en cada ejecución:

```text
--> [DL-portadas] prio=5 latencia=2539 ms
--> [DL-analitica] prio=1 latencia=1721 ms
--> [DL-sesion] prio=10 latencia=3577 ms
--> [DL-catalogo] prio=10 latencia=1483 ms
<-- [DL-catalogo] prio=10 LISTO: catalogo-metadatos
<-- [DL-analitica] prio=1 LISTO: analiticas
<-- [DL-portadas] prio=5 LISTO: portadas-assets
<-- [DL-sesion] prio=10 LISTO: sesion-y-perfil
[MAIN] Todos los módulos listos  ->  LOGIN HABILITADO
Tiempo total real:          3593 ms
Duración del más lento:     3577 ms
Suma de todas las latencias: 9320 ms
```

## Cómo compilar y enseñar una ejecución

Desde PowerShell, en la carpeta raíz del proyecto:

```powershell
mvn -f Demo\pom.xml clean test
java -cp Demo\target\classes org.example.udit.flix.MainUDITflix
```

Para el vídeo conviene ejecutar el segundo comando dos veces y comparar las latencias y el orden de finalización. No afirmar que las prioridades se comportan igual en todas las ejecuciones.

## Cosas que el vídeo no debe afirmar

- Que la aplicación descarga realmente archivos o usa internet: el programa solo simula la latencia.
- Que `MAX_PRIORITY` hace que un módulo siempre termine primero: Java no ofrece esa garantía.
- Que el tiempo total será siempre exactamente el de la tarea más lenta: hay sobrecarga y pequeñas variaciones de planificación.
- Que se implementaron una pantalla de carga demonio, una interfaz de login, estados observados con `getState()` o procesamiento intensivo de CPU: son funcionalidades que no están implementadas en este proyecto.
- Que el vídeo ya está grabado o que las reflexiones personales del alumno están verificadas.

## Prompt listo para Claude

```text
Lee toda la guía adjunta antes de redactar. Escribe en español un guion natural
para una defensa técnica del proyecto, con duración máxima de 2 minutos y 50
segundos. Está dirigido al profesor y a estudiantes que están aprendiendo Java
y concurrencia. Usa frases claras que se puedan decir en voz alta, sin tono
publicitario.

Estructura el guion por marcas de tiempo e incluye indicaciones breves de qué
mostrar en IntelliJ o en la consola. Debe cubrir: el problema de la precarga,
los cuatro módulos y sus prioridades, el papel de Runnable y Thread, por qué
se llama start() en un bucle y join() en otro, cómo se simula la latencia,
qué significan las mediciones y qué se observó sobre las prioridades. Incluye
una recomendación de ejecutar el programa dos veces para mostrar que las
latencias y el orden cambian.

Usa solo los datos de esta guía. Explica claramente que Thread.sleep() simula
una espera y que no hay descargas reales. No afirmes que las prioridades
garantizan el orden ni inventes una historia personal de depuración, datos
del autor o funcionalidades que no existen. Si hace falta hablar de una
experiencia personal, deja una nota breve para que el estudiante la complete.
No afirmes que no se usó IA: el proyecto se preparó con asistencia de IA y
autorización docente. Termina con una conclusión breve sobre lo aprendido.
```

## Pendiente antes de grabar o entregar

- Personalizar el guion de Claude y comprobar que el estudiante puede explicar cada parte del código.
- Completar los datos del autor y del repositorio en el README.
- Grabar con la propia voz; el reto pide una defensa de menos de 3 minutos.
- Sustituir cualquier nota pendiente por una reflexión personal real y confirmar con el docente cómo declarar la autorización para el uso de IA.
