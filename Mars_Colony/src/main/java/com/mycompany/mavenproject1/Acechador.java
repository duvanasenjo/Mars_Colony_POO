package com.mycompany.mavenproject1;

/**
 * Ataca por contacto: alcance 1, sin radio.
 *
 * @author dylnr
 */
public class Acechador extends Criatura {

    public Acechador(String nombre, int vidaMaxima, int daño, int nivel, int costo, double frecuencia) {
        super(nombre, vidaMaxima, daño, nivel, costo, 1, 0, frecuencia);
    }

    @Override
    public void atacar(Mapa mapa) {
        // Ataca por contacto al objetivo más cercano (distancia 1)
        atacarAlMasCercano(mapa, 1);
    }

    // Copia nueva del mismo tipo, con los mismos valores actuales
    // (vida llena, sin posición y con registro vacío).
    @Override
    public Criatura copiar() {
        Acechador copia = new Acechador(getNombre(), getVidaMaxima(), getDaño(), getNivel(), getCosto(), getFrecuencia());
        copiarDatosA(copia); // misión mínima, activo e imágenes
        return copia;
    }

    @Override
    public String getTipo() {
        return FabricaUnidades.ACECHADOR;
    }
}
