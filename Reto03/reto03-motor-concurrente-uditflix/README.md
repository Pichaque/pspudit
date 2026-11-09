# Reto 3 · Prueba B: Optimizador de descarga masiva

**Módulo:** 0490 · Programación de Servicios y Procesos (2.º DAM)  
**Autor/a:** completar  
**Repositorio:** completar con el enlace de GitHub  
**Vídeo de defensa (< 3 min):** pendiente de grabar y enlazar

## 1. Problema y solución

UDITflix necesita descargar los datos de sesión, el catálogo, las portadas y la analítica antes de mostrar el login. La versión secuencial alargaría la espera sumando las latencias. Esta solución simula los cuatro módulos en hilos concurrentes, registra las prioridades y solo habilita el login cuando todos terminan.

## 2. Plan y predicciones

Se reutilizan `Runnable`, `Thread`, `start()`, `join()`, `Random` y `Thread.sleep()` de la base de clase. La adaptación consiste en representar módulos de descarga, guardar los hilos en una lista y asignar prioridades.

1. Crear las tareas y asignar a cada una su latencia aleatoria.
2. Construir los hilos con nombre propio y prioridad.
3. Guardarlos en `ArrayList<Thread>` y lanzarlos con un bucle.
4. Esperarlos con un segundo bucle que use `join()`.
5. Calcular las latencias y habilitar el login.

Predicciones teóricas (no anotadas antes de comenzar la implementación): las cuatro tareas concurrentes deberían tardar aproximadamente lo mismo que la más lenta, entre 1 y 4 segundos más la sobrecarga; en secuencial, entre 4 y 16 segundos. El orden dependerá de las latencias y del planificador. La prioridad no garantiza que un hilo termine antes.

## 3. Arquitectura y conceptos

```text
main ──start()──► DL-catalogo ─┐
     ──start()──► DL-sesion   ─┤
     ──start()──► DL-portadas ─┼──► join() ──► LOGIN HABILITADO
     ──start()──► DL-analitica ┘
```

`TareaDescarga` implementa `Runnable` y simula una descarga. `MainUDITflix` crea las tareas, ajusta las prioridades, guarda los `Thread` en una lista y coordina su inicio y espera. Cada módulo se añade con una sola llamada a `agregarModulo`; el mismo código de lanzamiento y espera sirve para cualquier tamaño de lista.

| Concepto | Uso en este programa |
|---|---|
| `Runnable` | Define el trabajo simulado de una descarga. |
| `Thread` | Ejecuta cada tarea en su propio hilo. |
| `start()` | Inicia la ejecución concurrente; no equivale a llamar directamente a `run()`. |
| `run()` | Imprime la traza, duerme la latencia asignada y comunica que el módulo está listo. |
| `join()` | Hace que `main` espere a que cada hilo termine antes de habilitar el login. |
| `getPriority()` | Informa la prioridad al comienzo y al final de cada tarea. |
| `Thread.sleep()` | Simula latencia de red; el hilo queda esperando, no consumiendo CPU activamente. |
| `setPriority()` | Asigna la prioridad indicada antes de llamar a `start()`. |
| `setDaemon()` | No se usa; la pantalla de carga demonio era una ampliación opcional. |

| Hilo | Módulo | Prioridad |
|---|---|---:|
| `DL-catalogo` | `catalogo-metadatos` | `MAX_PRIORITY` (10) |
| `DL-sesion` | `sesion-y-perfil` | `MAX_PRIORITY` (10) |
| `DL-portadas` | `portadas-assets` | `NORM_PRIORITY` (5) |
| `DL-analitica` | `analiticas` | `MIN_PRIORITY` (1) |

## 4. Ejecución

Desde IntelliJ IDEA, ejecutar `org.example.udit.flix.MainUDITflix` en el módulo `Demo`. También se puede compilar y probar desde la raíz del proyecto:

```powershell
mvn -f Demo\pom.xml test
```

Para ejecutar después de compilar:

```powershell
java -cp Demo\target\classes org.example.udit.flix.MainUDITflix
```

- Java configurado en el proyecto: 17 o superior (`source` y `target` 17).
- Entorno de las pruebas documentadas: Windows 11 Home, Java 25.0.1, 12 procesadores lógicos.
- Archivos externos necesarios: ninguno.

## 5. Diario de decisiones y validación

La implementación conserva una colección de tareas junto a `ArrayList<Thread>` para poder calcular la latencia máxima y la suma sin duplicar la configuración de los cuatro módulos. Las tres ejecuciones de prueba confirmaron que los cuatro hilos se lanzan antes de esperar por ellos, que el login aparece después de los `join()` y que el tiempo real queda cerca de la latencia máxima.

## 6. Evidencias y análisis del planificador

Los valores corresponden a tres ejecuciones consecutivas de `MainUDITflix` en el entorno indicado arriba. El orden de latencias en cada celda es catálogo / sesión / portadas / analítica.

| Ejecución | Latencias (ms) | Orden de finalización | Tiempo total real | Más lento | Suma |
|---:|---|---|---:|---:|---:|
| 1 | 1382 / 1551 / 1498 / 2206 | catálogo → portadas → sesión → analítica | 2217 ms | 2206 ms | 6637 ms |
| 2 | 1483 / 3577 / 2539 / 1721 | catálogo → analítica → portadas → sesión | 3593 ms | 3577 ms | 9320 ms |
| 3 | 2283 / 2515 / 3440 / 2630 | catálogo → sesión → analítica → portadas | 3455 ms | 3440 ms | 10868 ms |

En las tres muestras, el tiempo real concurrente quedó muy cerca de la latencia más larga y por debajo de la suma de las cuatro latencias.  
El orden de finalización cambió entre ejecuciones porque las latencias son aleatorias y los hilos se planifican de forma no determinista.  
No se observa una ventaja consistente para los módulos críticos: en la segunda ejecución terminó último un hilo de prioridad máxima, mientras que en la primera la analítica, de prioridad mínima, fue la última.  
La prioridad de Java es una sugerencia al planificador, no una garantía de orden ni de finalización.  
Además, estas tareas pasan la mayor parte del tiempo en `sleep()`, por lo que compiten poco por el procesador.  
Con cuatro hilos y 12 procesadores lógicos, estas muestras no aíslan el efecto de la prioridad sobre trabajo intensivo de CPU.  
Para estudiar ese efecto haría falta repetir el experimento con una fase de cálculo y más mediciones.

Traza representativa de la segunda ejecución:

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

### Predicciones y resultados

La predicción de que el tiempo concurrente se aproximaría al módulo más lento se sostuvo en las tres ejecuciones. La suma secuencial teórica fue mayor en todos los casos. También se confirmó que no se puede predecir el orden de finalización a partir de las prioridades.

## 7. Preguntas de metacognición técnica

### 7.1 Preguntas comunes

1. **¿Por qué el tiempo total se parece al del hilo más lento y no a la suma?**  
   `start()` pone cada hilo a trabajar sin bloquear `main`. Los cuatro periodos de espera se solapan. Como `main` hace `join()` con todos, debe esperar hasta que termine el último; el tiempo queda cerca de la latencia mayor, más la pequeña sobrecarga de arranque y coordinación.

2. **¿Qué diferencia hay entre `start()` y `run()`?**  
   `start()` inicia un hilo nuevo que ejecuta `run()` de forma concurrente. Una llamada directa a `run()` ejecuta ese método en el hilo que la invoca, normalmente `main`, y no crea concurrencia.

3. **¿Qué pasaría sin `join()` o si se pusiera tras cada `start()`?**  
   Sin `join()`, `main` podría anunciar el login antes de que las descargas terminaran. Si cada `join()` se pone justo después de su `start()`, `main` espera a cada tarea antes de lanzar la siguiente y se pierde el paralelismo efectivo.

4. **¿Qué cambia entre duraciones fijas y aleatorias?**  
   Las fijas facilitan repetir y comparar exactamente la misma ejecución. Las aleatorias representan variabilidad y hacen que los tiempos y el orden puedan cambiar; para compararlas hay que registrar cada latencia.

5. **¿Por qué cambia el orden de finalización? ¿Se puede garantizar uno?**  
   Cada tarea recibe una latencia distinta y el sistema operativo decide cuándo ejecutar los hilos que están listos. Para imponer un orden habría que coordinar explícitamente las tareas (por ejemplo, esperar una antes de iniciar la siguiente); la prioridad no garantiza ese orden.

6. **¿Qué problema costó más depurar y cómo se resolvió?**  
   Durante una prueba desde PowerShell, una opción de codificación se interpretó como nombre de clase y Java no pudo iniciar el programa. Se identificó como un problema al pasar argumentos al comando, no como un fallo del código. Se repitió con el comando simple `java -cp ...` que figura en este README; Maven compiló correctamente y las tres ejecuciones finalizaron.

7. **¿Tendría sentido crear 500 `Thread`?**  
   No necesariamente: cada hilo consume recursos y una gran cantidad aumenta la sobrecarga de planificación. Para muchas tareas conviene limitar la concurrencia con un pool, por ejemplo `ExecutorService`.

### 7.2 Estados y espía (Prueba A)

No aplica a esta entrega: corresponde a la Prueba A y el espía no está implementado.

### 7.3 ArrayList y prioridades (Prueba B)

11. **¿Qué dificultad tiene gestionar `ArrayList<Thread>`?**  
    Se usan dos bucles separados: primero se llaman todos los `start()` y después se hace `join()` a cada hilo. Si se mezclan en un solo bucle, cada hilo se espera antes de iniciar el siguiente. Para añadir un módulo se añade una llamada a `agregarModulo`; los bucles y los cálculos no cambian.

12. **¿Qué diferencias se observaron con prioridades distintas?**  
    Las trazas muestran prioridades 10, 5 y 1, pero las tres muestras no tienen un patrón en el que los críticos acaben siempre antes. En la segunda ejecución, por ejemplo, la sesión crítica terminó la última; en la primera, la analítica acabó la última.

13. **Si la diferencia fue pequeña, ¿a qué puede deberse?**  
    Hay cuatro tareas y 12 procesadores lógicos en el entorno medido. Además, `sleep()` simula espera y no trabajo constante de CPU, así que las prioridades apenas tienen oportunidad de influir en una competición por procesador.

14. **¿Se puede confiar en las prioridades para garantizar que algo se ejecute antes?**  
    No. Java deja que la JVM y el sistema operativo interpreten las prioridades. Para garantizar requisitos de orden hay que usar coordinación y sincronización explícitas, no prioridades.

15. **¿Por qué termina el programa si la pantalla de carga no acaba?**  
    La ampliación de pantalla de carga demonio no está implementada. Si se añadiera con `setDaemon(true)`, ese hilo no impediría que la JVM terminase cuando hayan terminado todos los hilos no demonio.

## 8. Ejemplo en el mundo real

Una aplicación de streaming puede precargar en paralelo la sesión del usuario, el catálogo y las imágenes para reducir el tiempo hasta mostrar la pantalla principal. La puerta del login de este ejercicio representa la coordinación: la interfaz no declara lista la precarga hasta que han terminado las tareas requeridas.

## 9. Autoevaluación

Marcar honestamente después de revisar y poder explicar el código:

| Puedo explicar… | No | Más o menos | Sí |
|---|:-:|:-:|:-:|
| `Runnable` y `Thread` | ☐ | ☐ | ☐ |
| Por qué `start()` no espera a que el hilo acabe | ☐ | ☐ | ☐ |
| Qué hace `join()` y dónde se usa | ☐ | ☐ | ☐ |
| Los estados de un hilo y cuándo cambia | ☐ | ☐ | ☐ |
| Por qué cambia el orden de finalización | ☐ | ☐ | ☐ |
| Qué garantizan las prioridades (y qué no) | ☐ | ☐ | ☐ |
| Qué es un hilo demonio | ☐ | ☐ | ☐ |

**Qué haría diferente y qué quiero preguntar en clase:** completar con una reflexión propia después de revisar las pruebas y el efecto limitado de las prioridades.

## 10. Autoría y defensa

Esta versión se elaboró con asistencia de IA generativa y con autorización docente indicada para este trabajo. Antes de entregar, el alumno debe revisar la solución, asegurarse de poder explicarla y grabar el vídeo de defensa con su propia voz. No se afirma que el vídeo esté realizado ni se declara que no se usó IA.

## 11. Estructura

```text
Demo/
├── pom.xml
└── src/main/java/org/example/udit/flix/
    ├── MainUDITflix.java
    └── TareaDescarga.java
README.md
README_plantilla_Reto3.md
```
