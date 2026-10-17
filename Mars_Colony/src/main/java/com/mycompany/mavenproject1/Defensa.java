package com.mycompany.mavenproject1;

/**
 *
 * @author duvan
 */
public abstract class Defensa extends UnidadCombate {

    // Número de versión fijo para que las partidas guardadas sigan abriendo
    private static final long serialVersionUID = 5472240925905990628L;

    public Defensa(String nombre, int vidaMaxima, int daño, int nivel,
                    int costo, int alcance, double frecuencia) {
        super(nombre, vidaMaxima, daño, nivel, costo, alcance, frecuencia);
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

    // Cada defensa concreta sabe crear una copia de SU MISMO tipo.
    // Se usa para colocar defensas en cada batalla a partir de las plantillas.
    public abstract Defensa copiar();
}
