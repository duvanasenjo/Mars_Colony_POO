package com.mycompany.mavenproject1;

/**
 * Ataca por contacto (decisión de diseño): alcance 1, sin radio.
 *
 * @author dylnr
 */
public class Volador extends Criatura {

    public Volador(String nombre, int vidaMaxima, int daño, int nivel, int costo, double frecuencia) {
        super(nombre, vidaMaxima, daño, nivel, costo, 1, 0, frecuencia);
    }

    // Única criatura aérea: ignora barreras terrestres
    @Override
    public boolean esAerea() {
        return true;
    }

    @Override
    public void atacar(Mapa mapa) {
        // Ataca por contacto al objetivo más cercano (distancia 1)
        atacarAlMasCercano(mapa, 1);
    }

    // El volador ignora las barreras terrestres: ataca defensas
    // y el núcleo, pero NO barreras (las pasa por encima).
    @Override
    public boolean puedeAtacarA(UnidadCombate otra) {
        return !otra.esCriatura() && !otra.esBarrera();
    }

    // Igual que las demás criaturas, pero si la casilla de enfrente
    // está ocupada, SALTA por encima (ignora barreras terrestres).
    @Override
    public void mover(Mapa mapa) {
        UnidadCombate destino = buscarDestino(mapa);
        if (destino == null) {
            return; // ya puede atacar o no hay objetivos
        }
        int f = getPosicion().getFila();
        int c = getPosicion().getColumna();
        int df = direccion(f, destino.getPosicion().getFila());
        int dc = direccion(c, destino.getPosicion().getColumna());
        if (mapa.mover(this, f + df, c + dc)) {
            return; // paso normal
        }
        if (mapa.mover(this, f + 2 * df, c + 2 * dc)) {
            return; // saltó por encima del obstáculo
        }
        darPasoHacia(mapa, destino.getPosicion()); // si no pudo saltar, intenta rodear
    }
}
