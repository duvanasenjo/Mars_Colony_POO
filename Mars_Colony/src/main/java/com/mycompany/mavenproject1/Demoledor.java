package com.mycompany.mavenproject1;

import java.util.ArrayList;

/**
 * Cuando tiene un objetivo pegado (distancia 1) explota: daña a todo
 * lo que puede atacar dentro de su radio y se destruye.
 *
 * @author dylnr
 */
public class Demoledor extends Criatura {

    public Demoledor(String nombre, int vidaMaxima, int daño, int nivel, int costo, double frecuencia, int radio) {
        super(nombre, vidaMaxima, daño, nivel, costo, 1, radio, frecuencia);
    }

    @Override
    public void atacar(Mapa mapa) {
        ArrayList<UnidadCombate> pegados = buscarObjetivos(mapa, 1);
        if (pegados.size() > 0) {
            ArrayList<UnidadCombate> enElRadio = buscarObjetivos(mapa, getRadio());
            for (UnidadCombate objetivo : enElRadio) {
                golpear(objetivo, getDaño());
            }
            destruir(); // explota y desaparece
        }
    }

    // Copia nueva del mismo tipo, con los mismos valores actuales
    // (vida llena, sin posición y con registro vacío).
    @Override
    public Criatura copiar() {
        Demoledor copia = new Demoledor(getNombre(), getVidaMaxima(), getDaño(), getNivel(), getCosto(), getFrecuencia(), getRadio());
        copiarDatosA(copia); // misión mínima, activo e imágenes
        return copia;
    }

    @Override
    public String getTipo() {
        return FabricaUnidades.DEMOLEDOR;
    }
}
