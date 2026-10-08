package com.mycompany.mavenproject1;

import java.util.ArrayList;

/**
 * Detona cuando una criatura entra en su radio: daña a TODAS las
 * criaturas que puede atacar dentro del radio y se destruye.
 *
 * @author duvan
 */
public class Impacto extends Defensa implements IAtacante {

    public Impacto(String nombre, int vidaMaxima, int daño, int nivel, int costo, double frecuencia, int radio) {
        super(nombre, vidaMaxima, daño, nivel, costo, 1, radio, frecuencia);
    }

    @Override
    public void atacar(Mapa mapa) {
        ArrayList<UnidadCombate> objetivos = buscarObjetivos(mapa, getRadio());
        if (objetivos.size() > 0) {
            for (UnidadCombate objetivo : objetivos) {
                golpear(objetivo, getDaño());
            }
            destruir(); // explota y desaparece
        }
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
