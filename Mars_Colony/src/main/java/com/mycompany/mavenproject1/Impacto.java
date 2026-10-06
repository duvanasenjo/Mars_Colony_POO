package com.mycompany.mavenproject1;

import java.util.ArrayList;

/**
 * Detona cuando una criatura entra en su radio: daña a TODAS las
 * criaturas que puede atacar dentro del radio y se destruye.
 *
 * @author duvan
 */
public class Impacto extends Defensa implements IAtacante {

    public Impacto(String nombre, int vidaMaxima, int daño, int nivel, int costo, double frecuencia, int radio) {
        super(nombre, vidaMaxima, daño, nivel, costo, 1, radio, frecuencia);
    }

    @Override
    public void atacar(Mapa mapa) {
        ArrayList<UnidadCombate> objetivos = buscarObjetivos(mapa, getRadio());
        if (objetivos.size() > 0) {
            for (UnidadCombate objetivo : objetivos) {
                golpear(objetivo, getDaño());
            }
            destruir(); // explota y desaparece
        }
    }
}
