package org.example;

import jdk.swing.interop.DragSourceContextWrapper;

import java.io.IOException;
import java.sql.SQLOutput;

public class PildoraParalelismo {
    public static void main (String[] args){
        System.out.println("===============================================");
        System.out.println("🚀 PILDORA TECNICA: SECUENCIAL VS PARALELO");
        System.out.println("===============================================\n");

        try {
            System.out.println(" INICIANDO EJECUCION SECUENCIAL..........");
            long inicioSecuencial = System.currentTimeMillis();

            System.out.println("        -> Lanzando proceso 1 (y esperando que muera....");
            Process p1= new ProcessBuilder("ping", "-n", "2", "127.0.0.1").start();
            p1.waitFor();//CUIDADO JAVA SE CONGELA AQU. EL PROCESO DOS AUN NO EXISTE
            //Una vez que el p1 termina porfin lanzamosel segundo proceso
            System.out.println("        -> Lanzando proceso 2 (y esperando que muera....");
            Process p2= new ProcessBuilder("ping", "-n", "2", "8.8.8.8").start();
            p2.waitFor();//JAVA SE VUELVE A CONGELAR

            long finSecuncial = System.currentTimeMillis();
            System.out.println("⏰⏰ TIEMPO TOTAL SECUENCIAL " +(finSecuncial -inicioSecuencial));

            //2: EL CAMINO PARALELO (EJECUCION SOLAPADA)
            System.out.println("===============================================");
            System.out.println("INICIANDO EJECUCION PARALELA.........");
            System.out.println("===============================================");

            //RESETEO EL CRONOMETRO
            long inicioParalelo = System.currentTimeMillis();

            //PASO A LANZAMOS PROCESOS ( TODOS A LA VEZ )
            System.out.println("                -> Lnzando proceso 3 (¡No esperamos!");
            Process p3 = new   ProcessBuilder("ping", "-n", "2", "8.8.8.8").start();
            System.out.println("                -> Lnzando proceso 4 (¡No esperamos!");
            Process p4 = new   ProcessBuilder("ping", "-n", "2", "127.0.0.1").start();
            //PASO B LE DECIMOS A JAVA QUE RECOJA LOS RESLTADOS
            // COMO YA ESTAS CORRIENDO SIMULTANEAMENTE EN EL SO, EL TIEMPO DE ESPERA SE SOLAPA
            System.out.println("          -> BLoqueando java para recoger resultados");
            p3.waitFor();
            p4.waitFor();

            long finParalelo = System.currentTimeMillis();
            System.out.println("⏰⏰ TIEMPO TOTAL DE LA SECUENCIA: " + (finParalelo-inicioParalelo));


        }catch (IOException e){
            System.out.println("ERROR: no se ha podido lanzar el proceso");
        }catch (InterruptedException e){
            System.out.println("ERROR: La espera ha sido interrumpida");
        }
    }
}
