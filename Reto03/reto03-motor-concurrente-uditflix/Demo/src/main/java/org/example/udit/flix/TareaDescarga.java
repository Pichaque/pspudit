package org.example.udit.flix;

/**
 * Simula la descarga de un módulo de UDITflix.
 */
public class TareaDescarga implements Runnable {

    private final String nombreModulo;
    private final int latenciaMs;

    public TareaDescarga(String nombreModulo, int latenciaMs) {
        this.nombreModulo = nombreModulo;
        this.latenciaMs = latenciaMs;
    }

    @Override
    public void run() {
        Thread hiloActual = Thread.currentThread();
        System.out.println("--> [" + hiloActual.getName() + "] prio="
                + hiloActual.getPriority() + " latencia=" + latenciaMs + " ms");

        try {
            Thread.sleep(latenciaMs);
        } catch (InterruptedException e) {
            System.err.println("!--> [" + hiloActual.getName() + "] CANCELADO: "
                    + nombreModulo + " (interrumpido).");
            hiloActual.interrupt();
            return;
        }

        System.out.println("<-- [" + hiloActual.getName() + "] prio="
                + hiloActual.getPriority() + " LISTO: " + nombreModulo);
    }

    public int getLatenciaMs() {
        return latenciaMs;
    }
}
