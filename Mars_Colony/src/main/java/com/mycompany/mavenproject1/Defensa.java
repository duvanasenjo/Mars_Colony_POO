package com.mycompany.mavenproject1;

/**
 *
 * @author duvan
 */
public abstract class Defensa extends UnidadCombate {

    public Defensa(String nombre, int vidaMaxima, int daño, int nivel,
                    int costo, int alcance, int radio, double frecuencia) {
        super(nombre, vidaMaxima, daño, nivel, costo, alcance, radio, frecuencia);
    }

    // Por defecto una defensa NO puede atacar unidades aéreas.
    // Las antiaéreas (Alcance_medio y Dron) lo sobrescriben.
    public boolean puedeAtacarAereos() {
        return false;
    }

    // Una defensa solo ataca criaturas.
    // A las aéreas, solo si es antiaérea.
    @Override
    public boolean puedeAtacarA(UnidadCombate otra) {
        if (!otra.esCriatura()) {
            return false;
        }
        if (otra.esAerea() && !puedeAtacarAereos()) {
            return false;
        }
        return true;
    }
}
