package com.mycompany.mavenproject1;

/**
 *
 * @author duvan
 */
public class Dron extends Defensa implements IAtacante, IMovibles {

    public Dron(String nombre, int vidaMaxima, int daño, int nivel, int costo) {
        super(nombre, vidaMaxima, daño, nivel, costo);
    }

    @Override
    public void atacar(UnidadCombate objetivo) {
        // TODO: se implementa en el paso 4 (ataques y movimiento)
    }

    @Override
    public void mover() {
        // TODO: se implementa en el paso 4 (ataques y movimiento)
    }
}
