package com.mycompany.mavenproject1;

import java.io.Serializable;

/**
 * Un eslabón de la ColaTurnos: guarda UNA unidad que pidió su turno
 * y apunta al nodo que va detrás de ella en la fila.
 *
 * @author dylnr
 */
public class NodoTurno implements Serializable {

    private UnidadCombate unidad;
    private NodoTurno siguiente;   // null si es el último de la fila

    public NodoTurno(UnidadCombate unidad) {
        this.unidad = unidad;
        this.siguiente = null;
    }

    public UnidadCombate getUnidad() {
        return unidad;
    }

    public NodoTurno getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoTurno siguiente) {
        this.siguiente = siguiente;
    }
}
