package com.mycompany.mavenproject1;

/**
 * Dispara a distancia: alcance configurable, sin radio.
 *
 * @author duvan
 */
public class Alcance_medio extends Defensa implements IAtacante {

    public Alcance_medio(String nombre, int vidaMaxima, int daño, int nivel, int costo, double frecuencia, int alcance) {
        super(nombre, vidaMaxima, daño, nivel, costo, alcance, 0, frecuencia);
    }

    // Defensa antiaérea: puede atacar voladores
    @Override
    public boolean puedeAtacarAereos() {
        return true;
    }

    @Override
    public void atacar(Mapa mapa) {
        // Dispara al objetivo más cercano dentro de su alcance
        atacarAlMasCercano(mapa, getAlcance());
    }

    // En su turno solo ataca (no se mueve)
    @Override
    public void jugarTurno(Mapa mapa) {
        atacar(mapa);
    }
}
