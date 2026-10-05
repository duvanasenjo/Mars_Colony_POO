package com.mycompany.mavenproject1;

/**
 *
 * @author dylnr
 */
public abstract class Criatura extends UnidadCombate implements IAtacante, IMovibles {

    public Criatura(String nombre, int vidaMaxima, int daño, int nivel) {
        super(nombre, vidaMaxima, daño, nivel);
    }

    // atacar() y mover() NO se implementan aquí:
    // Java obliga a cada subclase a implementarlos a su manera.
}
