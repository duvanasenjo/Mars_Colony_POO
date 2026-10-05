package com.mycompany.mavenproject1;

/**
 *
 * @author duvan
 */
public class Alcance_medio extends Defensa implements IAtacante {

    public Alcance_medio(String nombre, int vidaMaxima, int daño, int nivel, int costo) {
        super(nombre, vidaMaxima, daño, nivel, costo);
    }

    @Override
    public void atacar(UnidadCombate objetivo) {
        // TODO: se implementa en el paso 4 (ataques y movimiento)
    }
}
