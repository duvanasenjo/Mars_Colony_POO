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

    // Copia nueva del mismo tipo, con los mismos valores actuales
    // (vida llena, sin posición y con registro vacío).
    @Override
    public Defensa copiar() {
        Alcance_medio copia = new Alcance_medio(getNombre(), getVidaMaxima(), getDaño(), getNivel(), getCosto(), getFrecuencia(), getAlcance());
        copiarDatosA(copia); // misión mínima, activo e imágenes
        return copia;
    }

    @Override
    public String getTipo() {
        return FabricaUnidades.ALCANCE_MEDIO;
    }
}
