package com.mycompany.mavenproject1;

import java.util.ArrayList;

/**
 * Ataca a VARIOS objetivos distintos en cada ciclo, dentro de su alcance.
 * Sin radio.
 *
 * @author duvan
 */
public class Ataque_multiple extends Defensa implements IAtacante {

    private int cantidadObjetivos; // a cuántos objetivos distintos ataca por ciclo

    public Ataque_multiple(String nombre, int vidaMaxima, int daño, int nivel, int costo, double frecuencia, int alcance,
                           int cantidadObjetivos) {
        super(nombre, vidaMaxima, daño, nivel, costo, alcance, 0, frecuencia);
        if (cantidadObjetivos < 1) {
            cantidadObjetivos = 1; // al menos ataca a uno
        }
        this.cantidadObjetivos = cantidadObjetivos;
    }

    @Override
    public void atacar(Mapa mapa) {
        ArrayList<UnidadCombate> objetivos = buscarObjetivos(mapa, getAlcance());
        int atacados = 0;
        while (atacados < cantidadObjetivos && objetivos.size() > 0) {
            UnidadCombate objetivo = elegirMasCercano(objetivos);
            golpear(objetivo, getDaño());
            objetivos.remove(objetivo); // para no golpear dos veces al mismo
            atacados = atacados + 1;
        }
    }

    public int getCantidadObjetivos() {
        return cantidadObjetivos;
    }

    // En su turno solo ataca (no se mueve)
    @Override
    public void jugarTurno(Mapa mapa) {
        atacar(mapa);
    }
}
