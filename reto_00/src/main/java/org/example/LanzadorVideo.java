package org.example;
import java.io.IOException;
public class LanzadorVideo {

        public static void main(String[] args) {

            // 1. Creamos el ProcessBuilder con los 4 textos separados
            ProcessBuilder pb = new ProcessBuilder(
                    "cmd",
                    "/c",
                    "start",
                    "https://www.youtube.com/watch?v=ZfNfr4bgy-s"
            );

            try {
                // 2. Lanzamos el proceso
                Process proceso = pb.start();

                // 3. NO usamos waitFor()
                System.out.println("¡Vídeo lanzado con éxito!");

            } catch (IOException e) {
                System.out.println("Error: no se pudo lanzar el vídeo.");
            }
        }
    }


