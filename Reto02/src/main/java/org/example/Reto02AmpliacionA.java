import java.io.IOException;
import java.util.ArrayList;

public class Reto02AmpliacionA {
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("       🛹 ORQUESTADOR DINÁMICO 🛹");
        System.out.println("==============================================");

        long inicio = System.currentTimeMillis();

        // 1. Array de servidores
        String[] ips = {"127.0.0.1", "192.0.2.1", "192.0.2.2"};
        ArrayList<Process> procesos = new ArrayList<>();

        try {
            System.out.println("INICIANDO COMPROBACIONES....");

            // 2. BUCLE 1 · LANZAMIENTO
            for (String ip : ips) {
                Process p = new ProcessBuilder("ping", "-n", "1", "-w", "3000", ip).start();
                procesos.add(p);
                System.out.println("    -> Lanzado ping a " + ip);
            }

            // 3. BUCLE 2 · RECOGIDA
            System.out.println("    -> Bloqueando Java hasta recoger resultados");
            int exitos = 0;
            int fallos = 0;
            for (Process p : procesos) {
                if (p.waitFor() == 0) {
                    exitos++;
                } else {
                    fallos++;
                }
            }
            System.out.println("Éxitos: " + exitos + " | Fallos: " + fallos);

            // 4. DECISIÓN FINAL según la tabla
            if (fallos == 0) {
                new ProcessBuilder("notepad.exe").start();   // 0 fallos
            } else if (exitos > 0) {
                new ProcessBuilder("calc.exe").start();      // éxitos y fallos
            } else {
                // Todo falla: se abre el YouTube
                new ProcessBuilder(
                        "cmd",
                        "/c",
                        "start",
                        "https://www.youtube.com/watch?v=798YCWbr-mA").start();
            }

        } catch (IOException e) {
            System.out.println("ERROR: no se ha podido lanzar el proceso");
        } catch (InterruptedException e) {
            System.out.println("ERROR: La espera ha sido interrumpida");
        }

        System.out.println("Tiempo total: " + (System.currentTimeMillis() - inicio) + " ms");
    }
}