package com.mycompany.mavenproject1;

import java.util.ArrayList;

/**
 * Cuando tiene un objetivo pegado (distancia 1) explota: daña a todo
 * lo que puede atacar dentro de su radio y se destruye.
 *
 * @author dylnr
 */
public class Demoledor extends Criatura {

    public Demoledor(String nombre, int vidaMaxima, int daño, int nivel, int costo, double frecuencia, int radio) {
        super(nombre, vidaMaxima, daño, nivel, costo, 1, radio, frecuencia);
    }

    @Override
    public void atacar(Mapa mapa) {
        ArrayList<UnidadCombate> pegados = buscarObjetivos(mapa, 1);
        if (pegados.size() > 0) {
            ArrayList<UnidadCombate> enElRadio = buscarObjetivos(mapa, getRadio());
            for (UnidadCombate objetivo : enElRadio) {
                golpear(objetivo, getDaño());
            }
            destruir(); // explota y desaparece
        }
    }
}
