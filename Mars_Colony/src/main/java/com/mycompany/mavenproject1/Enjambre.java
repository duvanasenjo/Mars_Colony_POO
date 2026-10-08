package com.mycompany.mavenproject1;

/**
 * Hace VARIOS ataques seguidos en cada ciclo, dentro de su alcance.
 * Antes de cada ataque vuelve a buscar, así que si un objetivo muere
 * pasa al siguiente. Sin radio.
 *
 * @author dylnr
 */
public class Enjambre extends Criatura {

    private int cantidadAtaques; // cuántos ataques hace por ciclo

    public Enjambre(String nombre, int vidaMaxima, int daño, int nivel, int costo, double frecuencia, int alcance,
                    int cantidadAtaques) {
        super(nombre, vidaMaxima, daño, nivel, costo, alcance, 0, frecuencia);
        if (cantidadAtaques < 1) {
            cantidadAtaques = 1; // al menos un ataque
        }
        this.cantidadAtaques = cantidadAtaques;
    }

    @Override
    public void atacar(Mapa mapa) {
        for (int i = 0; i < cantidadAtaques; i++) {
            atacarAlMasCercano(mapa, getAlcance());
        }
    }

    public int getCantidadAtaques() {
        return cantidadAtaques;
    }

    // Copia nueva del mismo tipo, con los mismos valores actuales
    // (vida llena, sin posición y con registro vacío).
    @Override
    public Criatura copiar() {
        Enjambre copia = new Enjambre(getNombre(), getVidaMaxima(), getDaño(), getNivel(), getCosto(), getFrecuencia(), getAlcance(), cantidadAtaques);
        copiarDatosA(copia); // misión mínima, activo e imágenes
        return copia;
    }

    @Override
    public String getTipo() {
        return FabricaUnidades.ENJAMBRE;
    }

    @Override
    public int getCantidad() {
        return getCantidadAtaques();
    }
}
