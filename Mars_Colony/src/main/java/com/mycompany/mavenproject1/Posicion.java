package com.mycompany.mavenproject1;

/**
 * Representa una coordenada (fila, columna) dentro del mapa.
 * Es inmutable: para mover una unidad se le asigna una Posicion nueva.
 *
 * @author dylnr
 */
public class Posicion {

    private final int fila;
    private final int columna;

    public Posicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    // Distancia en casillas. 1 = vecina (incluye diagonales).
    public int distanciaA(Posicion otra) {
        return Math.max(Math.abs(fila - otra.fila),
                        Math.abs(columna - otra.columna));
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    @Override
    public String toString() {
        return "(" + fila + ", " + columna + ")";
    }
}
