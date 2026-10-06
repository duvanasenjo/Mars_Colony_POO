package com.mycompany.mavenproject1;

/**
 *
 * @author dylnr
 */
public abstract class Criatura extends UnidadCombate implements IAtacante, IMovibles {

    public Criatura(String nombre, int vidaMaxima, int daño, int nivel,
                    int costo, int alcance, int radio, double frecuencia) {
        super(nombre, vidaMaxima, daño, nivel, costo, alcance, radio, frecuencia);
    }

    // atacar() NO se implementa aquí: cada criatura ataca a su manera.

    // Movimiento común de las criaturas terrestres: un paso hacia
    // su objetivo más cercano, salvo que ya lo tenga en su alcance.
    // Volador lo sobrescribe porque puede saltar barreras.
    @Override
    public void mover(Mapa mapa) {
        UnidadCombate destino = buscarDestino(mapa);
        if (destino != null) {
            darPasoHacia(mapa, destino.getPosicion());
        }
    }

    @Override
    public boolean esCriatura() {
        return true;
    }

    // Una criatura ataca todo lo que NO sea criatura:
    // defensas, barreras y el núcleo.
    @Override
    public boolean puedeAtacarA(UnidadCombate otra) {
        return !otra.esCriatura();
    }
}
