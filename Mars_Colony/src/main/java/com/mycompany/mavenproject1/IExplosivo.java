package com.mycompany.mavenproject1;

/**
 * Unidades que EXPLOTAN: dañan a todos los enemigos dentro de su radio
 * y se destruyen. Las implementan Impacto (defensa) y Demoledor (criatura).
 *
 * Una interfaz no puede tener atributos de objeto, por eso el radio
 * vive en cada clase que explota; la interfaz obliga a que lo tengan.
 *
 * @author dylnr
 */
public interface IExplosivo {

    // Radio mínimo de una explosión (casillas alrededor)
    public static final int RADIO_MINIMO = 1;

    // Daña a todo lo que puede atacar dentro del radio y se destruye
    void explotar(Mapa mapa);

    int getRadio();
}
