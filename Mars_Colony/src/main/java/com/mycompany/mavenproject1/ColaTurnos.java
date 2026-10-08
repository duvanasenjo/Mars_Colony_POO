package com.mycompany.mavenproject1;

import java.io.Serializable;

/**
 * COLA (fila) de turnos de la batalla, hecha con nodos.
 * El primero en entrar es el primero en salir (FIFO).
 *
 * Cada HiloUnidad ENCOLA a su unidad cuando quiere atacar o moverse,
 * y el HiloMapa la DESENCOLA para ejecutar su turno. Así el mapa lo
 * toca un solo hilo y los turnos se hacen en el orden en que se pidieron.
 *
 *   frente -> [Acechador] -> [Torre láser] -> [Enjambre] <- fin
 *
 * encolar y desencolar son synchronized: solo UN HILO a la vez puede
 * estar dentro de ese método; los demás esperan su turno. Sin eso,
 * si dos hilos encolan al mismo instante, uno puede borrar el enlace
 * del otro y ese turno se pierde.
 *
 * @author dylnr
 */
public class ColaTurnos implements Serializable {

    private NodoTurno frente;   // el primero de la fila (el próximo en salir)
    private NodoTurno fin;      // el último de la fila
    private int tamaño;

    public ColaTurnos() {
        this.frente = null;
        this.fin = null;
        this.tamaño = 0;
    }

    // Agrega la unidad al FINAL de la fila.
    public synchronized void encolar(UnidadCombate unidad) {
        NodoTurno nuevo = new NodoTurno(unidad);
        if (frente == null) {
            frente = nuevo;            // la fila estaba vacía: es el primero
        } else {
            fin.setSiguiente(nuevo);   // el último de antes apunta al nuevo
        }
        fin = nuevo;                   // el nuevo pasa a ser el último
        tamaño = tamaño + 1;
    }

    // Saca y devuelve la unidad del FRENTE de la fila (null si está vacía).
    public synchronized UnidadCombate desencolar() {
        if (frente == null) {
            return null;
        }
        UnidadCombate unidad = frente.getUnidad();
        frente = frente.getSiguiente();   // el segundo pasa a ser el primero
        if (frente == null) {
            fin = null;                   // se vació la fila
        }
        tamaño = tamaño - 1;
        return unidad;
    }

    // Mira quién está al frente SIN sacarlo (null si está vacía).
    public UnidadCombate verFrente() {
        if (frente == null) {
            return null;
        }
        return frente.getUnidad();
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int getTamaño() {
        return tamaño;
    }
}
