package org.example;

import java.io.IOException;

public class Reto02 {
    public static void main(String[] args){
        System.out.println("==============================================");
        System.out.println("       🛹EJECUCION DE PROCESOS 🛹");
        System.out.println("==============================================");
        try{
            System.out.println("INICIANDO EJECUCION....");
            System.out.println("           -> Lanzando primer proceso...");
            Process p1= new ProcessBuilder("ping", "-n", "2", "127.0.0.1").start();
            System.out.println("           -> Lanzando segundo proceso...");
            Process p2= new ProcessBuilder("ping", "-n", "2", "8.8.8.8").start();
            System.out.println("    -> Bloquendo Java");
            int Codsalida1=p1.waitFor();
            int Codsalida2=p2.waitFor();
            boolean semejanza= (Codsalida1==0 && Codsalida2==0);
            if (semejanza){
                ProcessBuilder pb= new ProcessBuilder(
                        "cmd",
                        "/c",
                        "start",
                        "https://www.youtube.com/watch?v=798YCWbr-mA");
                pb.start();
            }
            else {
                ProcessBuilder pb1= new ProcessBuilder("notepad.exe");
                pb1.start();
            }
        }catch (IOException e){
            System.out.println("ERROR: no se ha podido lanzar el proceso");
        }catch (InterruptedException e){
            System.out.println("ERROR: La espera ha sido interrumpida");
        }
        }
    }

