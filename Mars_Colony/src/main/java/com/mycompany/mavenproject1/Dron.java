package com.mycompany.mavenproject1;

/**
 * Se mueve y ataca dentro de su alcance, sin radio.
 *
 * @author duvan
 */
public class Dron extends Defensa implements IAtacante, IMovibles {

    public Dron(String nombre, int vidaMaxima, int daño, int nivel, int costo, double frecuencia, int alcance) {
        super(nombre, vidaMaxima, daño, nivel, costo, alcance, 0, frecuencia);
    }

    // Defensa antiaérea: puede atacar voladores
    @Override
    public boolean puedeAtacarAereos() {
        return true;
    }

    @Override
    public void atacar(Mapa mapa) {
        // Ataca al objetivo más cercano dentro de su alcance
        atacarAlMasCercano(mapa, getAlcance());
    }

    // Persigue a la criatura más cercana que puede atacar
    // hasta tenerla dentro de su alcance. Es terrestre: no salta.
    @Override
    public void mover(Mapa mapa) {
        UnidadCombate destino = buscarDestino(mapa);
        if (destino != null) {
            darPasoHacia(mapa, destino.getPosicion());
        }
    }

    // En su turno primero se acerca y luego ataca
    @Override
    public void jugarTurno(Mapa mapa) {
        mover(mapa);
        atacar(mapa);
    }
}
