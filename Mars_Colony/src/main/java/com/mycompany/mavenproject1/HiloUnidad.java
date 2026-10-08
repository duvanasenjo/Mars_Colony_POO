package com.mycompany.mavenproject1;

/**
 * El HILO de una unidad (defensa o criatura) durante la batalla.
 *
 * HEREDA de Thread y sobrescribe run(), que es lo que el hilo hace
 * al arrancar con start(). Cada unidad tiene su propio hilo, así que
 * cada una lleva su PROPIO RITMO según su frecuencia: una con
 * frecuencia 2.0 pide turno el doble de seguido que una con 1.0.
 *
 * Este hilo NO toca el mapa: solo duerme y, al despertar, se pone
 * en la cola de turnos. El que ejecuta el turno es el HiloMapa.
 *
 * @author dylnr
 */
public class HiloUnidad extends Thread {

    private UnidadCombate unidad;
    private Batalla batalla;

    public HiloUnidad(UnidadCombate unidad, Batalla batalla) {
        this.unidad = unidad;
        this.batalla = batalla;
    }

    @Override
    public void run() {
        while (batalla.isEnCurso() && unidad.estaViva()) {
            dormir(pausa());
            if (batalla.isEnCurso() && unidad.estaViva()) {
                batalla.getColaTurnos().encolar(unidad);   // "me toca atacar o moverme"
            }
        }
        // Al salir del while termina run() y el hilo se apaga solo.
    }

    // Milisegundos entre un turno y otro: más frecuencia = menos espera.
    private long pausa() {
        if (unidad.getFrecuencia() <= 0) {
            return Batalla.PAUSA_BASE;
        }
        return (long) (Batalla.PAUSA_BASE / unidad.getFrecuencia());
    }

    // Thread.sleep detiene ESTE hilo un rato (los demás siguen).
    private void dormir(long milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (Exception e) {
            // Si lo interrumpen mientras duerme, simplemente sigue
        }
    }

    public UnidadCombate getUnidad() {
        return unidad;
    }
}
