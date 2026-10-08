package com.mycompany.mavenproject1;

/**
 * Núcleo de oxígeno de la colonia. Es el objetivo de las criaturas:
 * si su vida llega a 0, se pierde la misión.
 *
 * Hereda de UnidadCombate para que las criaturas lo puedan atacar con
 * atacar(UnidadCombate objetivo), igual que a cualquier defensa.
 * No ataca ni se mueve, por eso no implementa IAtacante ni IMovibles.
 * No consume capacidad, por eso no tiene costo.
 *
 * @author dylnr
 */
public class Nucleo extends UnidadCombate {

    public Nucleo(int vidaMaxima) {
        super("Núcleo de oxígeno", vidaMaxima, 0, 1, 0, 0, 0, 0); // solo tiene vida
        // Sus imágenes van fijas: el núcleo no se configura en el catálogo
        setRutaImagenNormal("imagenes/nucleo_normal.gif");
        setRutaImagenMovimiento("imagenes/nucleo_movimiento.gif");
        setRutaImagenAtaque("imagenes/nucleo_ataque.gif");
    }

    @Override
    public String getTipo() {
        return "Núcleo";
    }
}
