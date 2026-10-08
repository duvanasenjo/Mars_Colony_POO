package com.mycompany.mavenproject1;

/**
 * El ÚNICO hilo que modifica el mapa durante la batalla.
 *
 * Saca de la cola, en orden, a cada unidad que pidió su turno y lo
 * ejecuta: jugarTurno (moverse y atacar, polimórfico) y luego quita
 * del mapa a los muertos. Como solo este hilo toca el mapa, nunca
 * quedan dos unidades en la misma casilla ni dos ataques se pisan.
 *
 * También revisa si la batalla terminó (núcleo destruido, todas las
 * criaturas muertas o tiempo agotado).
 *
 * @author dylnr
 */
public class HiloMapa extends Thread {

    private Batalla batalla;

    public HiloMapa(Batalla batalla) {
        this.batalla = batalla;
    }

    @Override
    public void run() {
        while (batalla.isEnCurso()) {
            UnidadCombate unidad = batalla.getColaTurnos().desencolar();
            if (unidad == null) {
                dormir(10);   // nadie pidió turno todavía: espera un poquito
            } else if (unidad.estaViva()) {
                unidad.jugarTurno(batalla.getMapa());   // cada tipo hace lo suyo
                batalla.quitarMuertos();
            }
            batalla.revisarFin();
        }
    }

    private void dormir(long milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (Exception e) {
            // Si lo interrumpen mientras duerme, simplemente sigue
        }
    }
}
