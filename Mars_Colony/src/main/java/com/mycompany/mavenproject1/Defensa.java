package com.mycompany.mavenproject1;

/**
 *
 * @author duvan
 */
public abstract class Defensa extends UnidadCombate {

    private int costo; // puntos de capacidad que cuesta colocarla

    public Defensa(String nombre, int vidaMaxima, int daño, int nivel, int costo) {
        super(nombre, vidaMaxima, daño, nivel);
        this.costo = costo;
    }

    public int getCosto() {
        return costo;
    }
}
