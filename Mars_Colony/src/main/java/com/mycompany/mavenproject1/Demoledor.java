package com.mycompany.mavenproject1;

import java.util.ArrayList;

/**
 * Cuando tiene un objetivo pegado (distancia 1) explota: daña a todo
 * lo que puede atacar dentro de su radio y se destruye.
 *
 * Es explosiva: implementa IExplosivo y es la única criatura con radio.
 *
 * @author dylnr
 */
public class Demoledor extends Criatura implements IExplosivo {

    // Número de versión fijo para que las partidas guardadas sigan abriendo
    private static final long serialVersionUID = 8009547620981431936L;

    private int radio;   // casillas alrededor que alcanza la explosión

    public Demoledor(String nombre, int vidaMaxima, int daño, int nivel, int costo, double frecuencia, int radio) {
        super(nombre, vidaMaxima, daño, nivel, costo, 1, frecuencia);
        if (radio < RADIO_MINIMO) {
            radio = RADIO_MINIMO;
        }
        this.radio = radio;
    }

    // Si ya tocó a alguien (distancia 1), explota.
    @Override
    public void atacar(Mapa mapa) {
        if (buscarObjetivos(mapa, 1).size() > 0) {
            explotar(mapa);
        }
    }

    // Daña a todo lo que puede atacar dentro del radio y desaparece.
    @Override
    public void explotar(Mapa mapa) {
        ArrayList<UnidadCombate> enElRadio = buscarObjetivos(mapa, getRadio());
        for (UnidadCombate objetivo : enElRadio) {
            golpear(objetivo, getDaño());
        }
        destruir();
    }

    // Nunca menos que el radio mínimo (las partidas guardadas antes de
    // este cambio no traen el radio y llegan en 0).
    @Override
    public int getRadio() {
        if (radio < RADIO_MINIMO) {
            return RADIO_MINIMO;
        }
        return radio;
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
