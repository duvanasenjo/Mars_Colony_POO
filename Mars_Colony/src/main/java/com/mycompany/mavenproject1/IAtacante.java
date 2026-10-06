package com.mycompany.mavenproject1;

/**
 * Unidades que pueden atacar. Cada una recibe el mapa,
 * elige ella misma a quién atacar y lo golpea.
 *
 * @author dylnr
 */
public interface IAtacante {
    void atacar(Mapa mapa);
}
