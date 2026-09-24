package org.example;

import java.util.ArrayList;

public class Plataforma {
    private ArrayList<Episodio> catalogo;
    public Plataforma() {
        this.catalogo = new ArrayList<>();
    }
    public void agregarEpisodio (Episodio e){
        this.catalogo.add(e);
    }
    public void procesarCatalogo(){
        for (Episodio ep : this.catalogo){
            ep.procesar();
        }


    }
}
