package com.mycompany.mavenproject1;

/**
 * Unidades que pueden cambiar de posición. Reciben el mapa
 * para saber hacia dónde moverse y qué casillas están libres.
 *
 * @author dylnr
 */
public interface IMovibles {
    void mover(Mapa mapa);
}
