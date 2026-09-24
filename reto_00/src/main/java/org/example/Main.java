package org.example;
import java.util.ArrayList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        Plataforma netflixUDit = new Plataforma();

        netflixUDit.agregarEpisodio(new Episodio("Video 1", 30));
        netflixUDit.agregarEpisodio(new Episodio("Video 2", 40));
        netflixUDit.agregarEpisodio(new Episodio("Video 3", 50));
        netflixUDit.agregarEpisodio(new Episodio("Video 4", 60));
        netflixUDit.agregarEpisodio(new Episodio("Video 5", 70));

        long incio = System.currentTimeMillis();

        netflixUDit.procesarCatalogo();

        long fin = System.currentTimeMillis();
        long totalSegundos = (fin -incio) / 1000;

        System.out.println("Tiempo total: " + totalSegundos + " segundos");

    }

}