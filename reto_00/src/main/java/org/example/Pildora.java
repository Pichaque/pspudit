package org.example;

// Clases de Java que necesitamos para leer lo que dice el proceso externo
// y para controlar los errores al lanzarlo
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Pildora {

    public static void main(String[] args) {

        // Cabecera: se imprime UNA sola vez, por eso va fuera del for
        System.out.println("========================================");
        System.out.println("        UDITFLIX - CATÁLOGO");
        System.out.println("========================================");

        // Matriz de dos dimensiones: 5 filas (contenidos) y 2 columnas
        // Columna 0 -> nombre del contenido
        // Columna 1 -> dirección que vamos a comprobar con ping
        // 127.0.0.1 es nuestro propio ordenador: siempre responde (ACTIVO)
        // 192.0.2.1 es una dirección reservada para pruebas: nunca responde (CAÍDO)
        String[][] servicios = {
                {"Series",       "192.0.2.1"},
                {"Películas",    "127.0.0.1"},
                {"Documentales", "127.0.0.1"},
                {"Anime",        "192.0.2.1"},
                {"Infantil",     "127.0.0.1"}
        };

        // Bucle for: recorre las filas de la matriz, una por contenido
        // i vale 0, 1, 2, 3 y 4. servicios.length es el número de filas (5)
        for (int i = 0; i < servicios.length; i++) {

            // "Interruptor" que empieza apagado: de momento no hemos visto respuesta.
            // Va dentro del for para que se reinicie en cada contenido;
            // si estuviera fuera, un ACTIVO afectaría a los siguientes
            boolean encontrado = false;

            // Lanzar un programa externo puede fallar, por eso usamos try/catch
            try {

                // Sacamos los datos de la fila actual de la matriz
                String nombre = servicios[i][0];
                String direccion = servicios[i][1];
                System.out.println("[CONTENIDO] " + nombre);

                // Preparamos el comando (todavía no se ejecuta):
                // "-n", "1"    -> hacer un solo intento
                // "-w", "1000" -> esperar máximo 1000 ms (1 segundo) la respuesta
                // direccion    -> cambia en cada vuelta (antes era 127.0.0.1 fijo)
                ProcessBuilder pb = new ProcessBuilder(
                        "ping", "-n", "1", "-w", "1000", direccion
                );

                // Unimos la salida normal y la de errores en un solo canal
                // para leer todo lo que diga el proceso desde un único sitio
                pb.redirectErrorStream(true);

                // Lanzamos el proceso de verdad: el sistema operativo crea el ping
                Process proceso = pb.start();

                // Mostramos el PID, el "DNI" del proceso (distinto en cada uno)
                System.out.println("PID: " + proceso.pid());

                // Nos conectamos a la salida del proceso para poder leerla:
                // getInputStream()    -> tubería por la que sale el texto (en bytes)
                // InputStreamReader   -> convierte los bytes en letras
                // BufferedReader      -> permite leer línea a línea
                BufferedReader lector = new BufferedReader(
                        new InputStreamReader(proceso.getInputStream())
                );

                // Aquí guardamos cada línea que leemos
                String linea;

                // Leemos línea a línea hasta que no quede nada (readLine devuelve null)
                // No imprimimos las líneas: solo buscamos una pista dentro de ellas
                while ((linea = lector.readLine()) != null) {
                    // TTL solo aparece en la respuesta real
                    // ("Respuesta desde ... TTL=128"), no en "Tiempo de espera agotado"
                    if (linea.contains("TTL")) {
                        encontrado = true;   // hemos visto respuesta: enciende el interruptor
                    }
                }

                // Esperamos a que el proceso termine del todo antes de decidir.
                // No usamos el código de salida porque en Windows ping puede
                // devolver 0 aunque la dirección no responda
                proceso.waitFor();

                // Decidimos el estado UNA vez, después de leer todo
                if (encontrado) {
                    System.out.println("ESTADO: ACTIVO");
                } else {
                    System.out.println("ESTADO: CAÍDO");
                }

            } catch (IOException e) {
                // Salta si no se puede lanzar el proceso (por ejemplo, no existe ping)
                System.out.println("No se pudo lanzar el proceso");
            } catch (InterruptedException e) {
                // Salta si algo interrumpe la espera de waitFor()
                System.out.println("La ejecución fue interrumpida");
            }

            // Línea en blanco para separar un contenido del siguiente
            System.out.println();
        }

        // Mensaje final: una sola vez, fuera del for
        System.out.println("========================================");
        System.out.println("     COMPROBACIÓN FINALIZADA");
        System.out.println("========================================");
    }
}