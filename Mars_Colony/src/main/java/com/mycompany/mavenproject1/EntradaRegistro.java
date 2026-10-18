package com.mycompany.mavenproject1;

import java.io.Serializable;

/**
 * Una línea del registro de combate: cuántos golpes y cuánto daño
 * hubo con OTRA unidad. Ejemplo: "Acechador: 3 golpes, 12 de daño".
 *
 * @author dylnr
 */
public class EntradaRegistro implements Serializable {

    private UnidadCombate otraUnidad;
    private int golpes;
    private int dañoTotal;

    public EntradaRegistro(UnidadCombate otraUnidad) {
        this.otraUnidad = otraUnidad;
        this.golpes = 0;
        this.dañoTotal = 0;
    }

    // Suma un golpe más y el daño que causó
    public void sumarGolpe(int daño) {
        golpes = golpes + 1;
        dañoTotal = dañoTotal + daño;
    }

    public UnidadCombate getOtraUnidad() {
        return otraUnidad;
    }

    public int getGolpes() {
        return golpes;
    }

    public int getDañoTotal() {
        return dañoTotal;
    }

    @Override
    public String toString() {
        String palabra = "golpes";
        if (golpes == 1) {
            palabra = "golpe";   // "1 golpe", "3 golpes"
        }
        return otraUnidad.getIdentificador() + ": " + golpes + " " + palabra + ", " + dañoTotal + " de daño";
    }
}
