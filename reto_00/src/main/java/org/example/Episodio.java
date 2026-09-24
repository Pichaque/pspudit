package org.example;

public class Episodio {

    //ATRIBUTOS: Variables privadas por seguridad (encapsulamiento)
    // Solo esta clase puede modificar directamente
    private String titulo;
    private int duracion;

    //Constructor: El molde que ejecuta al hacer un new Episodio
    public Episodio(String titulo, int duracion){
        this.titulo = titulo;
        this.duracion   =duracion;
    }

    //METODO: La accion que simula la carga lenta del servidor
    public void procesar(){
        System.out.println("Iniciando procesamiento de: " + this.titulo + "...");
        // El bloque tr-catch es OBLIGATORIO en java para pausar el hilo
        // Evita que el programa explote si el SO interrumpe la pausa
        try{
            //Pausamos la ejecucion en 200 milisegundos (2s)
            Thread.sleep(2000);
        }catch (InterruptedException e){
            System.out.println("Error al procesar episodio");
        }
        System.out.println("Completado: " + this.titulo);
    }
}
