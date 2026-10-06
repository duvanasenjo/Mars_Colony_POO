package com.mycompany.mavenproject1;

/**
 * Solo ataca criaturas adyacentes: alcance 1, sin radio.
 *
 * @author duvan
 */
public class Contacto extends Defensa implements IAtacante {

    public Contacto(String nombre, int vidaMaxima, int daño, int nivel, int costo, double frecuencia) {
        super(nombre, vidaMaxima, daño, nivel, costo, 1, 0, frecuencia);
    }

    @Override
    public void atacar(Mapa mapa) {
        // Ataca a la criatura más cercana que esté pegada (distancia 1)
        atacarAlMasCercano(mapa, 1);
    }

    // En su turno solo ataca (no se mueve)
    @Override
    public void jugarTurno(Mapa mapa) {
        atacar(mapa);
    }
}
