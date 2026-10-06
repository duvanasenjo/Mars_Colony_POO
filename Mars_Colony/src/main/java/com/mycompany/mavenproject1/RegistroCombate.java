package com.mycompany.mavenproject1;

import java.util.ArrayList;

/**
 * Historial de combate de UNA unidad:
 * - objetivosAtacados: a quién atacó
 * - atacantesRecibidos: quién la atacó
 * Hay una entrada por cada unidad distinta.
 *
 * @author dylnr
 */
public class RegistroCombate {

    private ArrayList<EntradaRegistro> objetivosAtacados;
    private ArrayList<EntradaRegistro> atacantesRecibidos;

    public RegistroCombate() {
        objetivosAtacados = new ArrayList<>();
        atacantesRecibidos = new ArrayList<>();
    }

    // "Yo ataqué a 'objetivo' y le hice 'daño'"
    public void registrarAtaque(UnidadCombate objetivo, int daño) {
        for (EntradaRegistro entrada : objetivosAtacados) {
            if (entrada.getOtraUnidad() == objetivo) {
                entrada.sumarGolpe(daño);
                return; // ya existía: solo sumamos y terminamos
            }
        }
        // Si llegamos aquí, es la primera vez que lo atacamos
        EntradaRegistro nueva = new EntradaRegistro(objetivo);
        nueva.sumarGolpe(daño);
        objetivosAtacados.add(nueva);
    }

    // "'atacante' me atacó y me hizo 'daño'"
    public void registrarRecibido(UnidadCombate atacante, int daño) {
        for (EntradaRegistro entrada : atacantesRecibidos) {
            if (entrada.getOtraUnidad() == atacante) {
                entrada.sumarGolpe(daño);
                return;
            }
        }
        EntradaRegistro nueva = new EntradaRegistro(atacante);
        nueva.sumarGolpe(daño);
        atacantesRecibidos.add(nueva);
    }

    // Se devuelve una COPIA de la lista: si alguien de afuera la modifica,
    // el historial real no cambia (encapsulamiento).
    public ArrayList<EntradaRegistro> getObjetivosAtacados() {
        return new ArrayList<>(objetivosAtacados);
    }

    public ArrayList<EntradaRegistro> getAtacantesRecibidos() {
        return new ArrayList<>(atacantesRecibidos);
    }
}
