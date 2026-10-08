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

    // Copia nueva del mismo tipo, con los mismos valores actuales
    // (vida llena, sin posición y con registro vacío).
    @Override
    public Criatura copiar() {
        Escupidor copia = new Escupidor(getNombre(), getVidaMaxima(), getDaño(), getNivel(), getCosto(), getFrecuencia(), getAlcance());
        copiarDatosA(copia); // misión mínima, activo e imágenes
        return copia;
    }

    @Override
    public String getTipo() {
        return FabricaUnidades.ESCUPIDOR;
    }
}
