package com.mycompany.mavenproject1;

/**
 * Progreso del comandante: su nombre, en qué misión va y cuánta
 * capacidad tiene su escuadrón. La capacidad pertenece al escuadrón,
 * no al mapa (por eso está aquí y no en Mapa ni en Batalla).
 *
 * @author dylnr
 */
public class Partida {

    public static final int CAPACIDAD_INICIAL = 20;
    public static final int CAPACIDAD_POR_MISION = 5;
    public static final int MISIONES_INICIALES = 10;

    private String nombreComandante;
    private int misionActual;
    private int capacidadTotal;

    public Partida(String nombreComandante) {
        this.nombreComandante = nombreComandante;
        this.misionActual = 1;
        this.capacidadTotal = CAPACIDAD_INICIAL;
    }

    // Suma el costo de las defensas colocadas en la batalla.
    // El núcleo no cuenta: no está en la lista de defensas.
    public int capacidadUsada(Batalla batalla) {
        int usada = 0;
        for (Defensa defensa : batalla.getDefensas()) {
            usada = usada + defensa.getCosto();
        }
        return usada;
    }

    public int capacidadRestante(Batalla batalla) {
        return capacidadTotal - capacidadUsada(batalla);
    }

    // Coloca la defensa solo si alcanza la capacidad.
    // Devuelve false si no alcanza o si la casilla no es válida.
    public boolean colocarDefensa(Batalla batalla, Defensa defensa, int fila, int columna) {
        if (defensa.getCosto() > capacidadRestante(batalla)) {
            return false; // no alcanza la capacidad
        }
        return batalla.agregarDefensa(defensa, fila, columna);
    }

    // Se llama cuando el jugador gana la misión y decide avanzar.
    // (Para repetir la misión simplemente NO se llama.)
    public void superarMision() {
        misionActual = misionActual + 1;
        capacidadTotal = capacidadTotal + CAPACIDAD_POR_MISION;
    }

    // true cuando ya pasó la misión 10: el jugador puede terminar
    // la campaña o seguir con misiones nuevas.
    public boolean terminoMisionesIniciales() {
        return misionActual > MISIONES_INICIALES;
    }

    public String getNombreComandante() {
        return nombreComandante;
    }

    public int getMisionActual() {
        return misionActual;
    }

    public int getCapacidadTotal() {
        return capacidadTotal;
    }
}
