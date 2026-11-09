package org.example.udit.flix;

import java.util.ArrayList;
import java.util.Random;

/**
 * Descarga en paralelo los módulos necesarios antes de habilitar el login.
 */
public class MainUDITflix {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("==================================================");
        System.out.println("   UDITflix · OPTIMIZADOR DE DESCARGA MASIVA       ");
        System.out.println("==================================================");

        Random azar = new Random();
        ArrayList<Thread> hilos = new ArrayList<>();
        ArrayList<TareaDescarga> tareas = new ArrayList<>();

        agregarModulo(hilos, tareas, azar, "DL-catalogo", "catalogo-metadatos", Thread.MAX_PRIORITY);
        agregarModulo(hilos, tareas, azar, "DL-sesion", "sesion-y-perfil", Thread.MAX_PRIORITY);
        agregarModulo(hilos, tareas, azar, "DL-portadas", "portadas-assets", Thread.NORM_PRIORITY);
        agregarModulo(hilos, tareas, azar, "DL-analitica", "analiticas", Thread.MIN_PRIORITY);

        long inicio = System.currentTimeMillis();
        System.out.println("\n[MAIN] Descargando " + hilos.size() + " módulos en paralelo...");
        for (Thread hilo : hilos) {
            hilo.start();
        }
        for (Thread hilo : hilos) {
            hilo.join();
        }

        long total = System.currentTimeMillis() - inicio;
        int masLento = 0;
        int suma = 0;
        for (TareaDescarga tarea : tareas) {
            masLento = Math.max(masLento, tarea.getLatenciaMs());
            suma += tarea.getLatenciaMs();
        }

        System.out.println("\n==================================================");
        System.out.println("[MAIN] Todos los módulos listos  ->  LOGIN HABILITADO");
        System.out.println("Tiempo total real:          " + total + " ms");
        System.out.println("Duración del más lento:     " + masLento + " ms");
        System.out.println("Suma de todas las latencias: " + suma + " ms");
        System.out.println("==================================================");
    }

    private static void agregarModulo(ArrayList<Thread> hilos,
                                      ArrayList<TareaDescarga> tareas,
                                      Random azar,
                                      String nombreHilo,
                                      String nombreModulo,
                                      int prioridad) {
        int latenciaMs = 1000 + azar.nextInt(3001);
        TareaDescarga tarea = new TareaDescarga(nombreModulo, latenciaMs);
        Thread hilo = new Thread(tarea, nombreHilo);
        hilo.setPriority(prioridad);
        tareas.add(tarea);
        hilos.add(hilo);
    }
}
