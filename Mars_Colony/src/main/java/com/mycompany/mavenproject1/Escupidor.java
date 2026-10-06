package com.mycompany.mavenproject1;

/**
 * Ataca a distancia: alcance configurable, sin radio.
 *
 * @author dylnr
 */
public class Escupidor extends Criatura {

    public Escupidor(String nombre, int vidaMaxima, int daño, int nivel, int costo, double frecuencia, int alcance) {
        super(nombre, vidaMaxima, daño, nivel, costo, alcance, 0, frecuencia);
    }

    @Override
    public void atacar(Mapa mapa) {
        // Escupe al objetivo más cercano dentro de su alcance
        atacarAlMasCercano(mapa, getAlcance());
    }
}
