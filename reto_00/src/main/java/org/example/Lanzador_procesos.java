package org.example;

import java.io.IOException;

public class Lanzador_procesos {
    public static void main (String[] args) {
        System.out.println("Solicitando al SO la creacion de un proceso nativo.....");

        // ProcessBuilder es el puente entre java y es SO
        // Preparamos el comando ( en Windows, notepad.exe es el block de notas).

        ProcessBuilder pb = new ProcessBuilder("notepad.exe");
        try{
            //2. start() es el gatillo. Pide al planificador del SO que cree el proceso
            Process proceso = pb.start();
            System.out.println("Proceso en ejecucion. PID " + proceso.pid());
            //3. waitFor() pausa nuestro hilo de java hasta que el usuario cierre el bloc de notas
            int salida = proceso.waitFor();
            //0 significa cierre limpio. CUalquier otro numero indica error o cierre forzoso
            System.out.println("El proceso externo finalizo con codigo: " + salida);

        }catch (IOException e){
            System.out.println("Error: el sistema operativo no encuentra el programa ");
        }catch (InterruptedException e){
            System.out.println("Error: El proceso fue interrumpido bruscamente");
        }
    }
}
