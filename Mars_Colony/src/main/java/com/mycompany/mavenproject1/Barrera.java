package com.mycompany.mavenproject1;

/**
 * Bloquea el paso y absorbe daño. No ataca ni se mueve:
 * daño, alcance, radio y frecuencia quedan fijos en 0.
 *
 * @author duvan
 */
public class Barrera extends Defensa {

    public Barrera(String nombre, int vidaMaxima, int nivel, int costo) {
        super(nombre, vidaMaxima, 0, nivel, costo, 0, 0, 0);
    }

    @Override
    public boolean esBarrera() {
        return true;
    }

    // Aunque es una Defensa, la barrera no ataca a nadie.
    @Override
    public boolean puedeAtacarA(UnidadCombate otra) {
        return false;
    }

    // Copia nueva del mismo tipo, con los mismos valores actuales
    // (vida llena, sin posición y con registro vacío).
    @Override
    public Defensa copiar() {
        Barrera copia = new Barrera(getNombre(), getVidaMaxima(), getNivel(), getCosto());
        copiarDatosA(copia); // misión mínima, activo e imágenes
        return copia;
    }

    @Override
    public String getTipo() {
        return FabricaUnidades.BARRERA;
    }
}
