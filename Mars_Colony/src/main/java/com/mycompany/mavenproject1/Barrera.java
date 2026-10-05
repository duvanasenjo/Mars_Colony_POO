package com.mycompany.mavenproject1;

/**
 *
 * @author duvan
 */
public class Barrera extends Defensa {

    public Barrera(String nombre, int vidaMaxima, int daño, int nivel, int costo) {
        super(nombre, vidaMaxima, daño, nivel, costo);
    }

    // No ataca ni se mueve: solo bloquea el paso y absorbe daño.
}
