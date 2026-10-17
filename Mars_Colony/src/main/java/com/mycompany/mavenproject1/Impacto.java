package com.mycompany.mavenproject1;

import java.util.ArrayList;

/**
 * Detona cuando una criatura entra en su radio: daña a TODAS las
 * criaturas que puede atacar dentro del radio y se destruye.
 *
 * Es explosiva: implementa IExplosivo y es la única defensa con radio.
 *
 * @author duvan
 */
public class Impacto extends Defensa implements IAtacante, IExplosivo {

    // Número de versión fijo para que las partidas guardadas sigan abriendo
    private static final long serialVersionUID = -6507914813797665347L;

    private int radio;   // casillas alrededor que alcanza la explosión

    public Impacto(String nombre, int vidaMaxima, int daño, int nivel, int costo, double frecuencia, int radio) {
        super(nombre, vidaMaxima, daño, nivel, costo, 1, frecuencia);
        if (radio < RADIO_MINIMO) {
            radio = RADIO_MINIMO;
        }
        this.radio = radio;
    }

    // Si hay alguna criatura dentro del radio, explota.
    @Override
    public void atacar(Mapa mapa) {
        if (buscarObjetivos(mapa, getRadio()).size() > 0) {
            explotar(mapa);
        }
    }

    // Daña a todas las criaturas dentro del radio y desaparece.
    @Override
    public void explotar(Mapa mapa) {
        ArrayList<UnidadCombate> objetivos = buscarObjetivos(mapa, getRadio());
        for (UnidadCombate objetivo : objetivos) {
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

    // En su turno solo ataca (no se mueve)
    @Override
    public void jugarTurno(Mapa mapa) {
        atacar(mapa);
    }

    // Copia nueva del mismo tipo, con los mismos valores actuales
    // (vida llena, sin posición y con registro vacío).
    @Override
    public Defensa copiar() {
        Impacto copia = new Impacto(getNombre(), getVidaMaxima(), getDaño(), getNivel(), getCosto(), getFrecuencia(), getRadio());
        copiarDatosA(copia); // misión mínima, activo e imágenes
        return copia;
    }

    @Override
    public String getTipo() {
        return FabricaUnidades.IMPACTO;
    }
}
