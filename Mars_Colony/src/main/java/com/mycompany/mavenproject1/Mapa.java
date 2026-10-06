package com.mycompany.mavenproject1;

import java.util.ArrayList;

/**
 * Cuadrícula de la colonia. Es la ÚNICA clase que modifica la matriz:
 * aquí viven todas las reglas de colocación (límites, una unidad por celda,
 * núcleo fijo en el centro).
 *
 * Los métodos que cambian el mapa devuelven true si se pudo hacer
 * y false si alguna regla lo impide.
 *
 * @author dylnr
 */
public class Mapa {

    public static final int TAMAÑO = 25;

    private final UnidadCombate[][] celdas; // null = celda vacía
    private final Nucleo nucleo;

    public Mapa(int vidaNucleo) {
        celdas = new UnidadCombate[TAMAÑO][TAMAÑO];
        nucleo = new Nucleo(vidaNucleo);
        int centro = TAMAÑO / 2; // 12 en un mapa de 25
        celdas[centro][centro] = nucleo;
        nucleo.setPosicion(new Posicion(centro, centro));
    }

    // ¿La coordenada está dentro del mapa?
    public boolean estaDentro(int fila, int columna) {
        return fila >= 0 && fila < TAMAÑO && columna >= 0 && columna < TAMAÑO;
    }

    // ¿La celda está dentro del mapa y vacía?
    public boolean estaLibre(int fila, int columna) {
        return estaDentro(fila, columna) && celdas[fila][columna] == null;
    }

    // Lo que hay en la celda (null si está vacía o fuera del mapa)
    public UnidadCombate obtener(int fila, int columna) {
        if (!estaDentro(fila, columna)) {
            return null;
        }
        return celdas[fila][columna];
    }

    public boolean colocar(UnidadCombate unidad, int fila, int columna) {
        if (unidad == null || unidad.getPosicion() != null) {
            return false; // no existe o ya está colocada en otra celda
        }
        if (!estaLibre(fila, columna)) {
            return false; // fuera del mapa u ocupada
        }
        celdas[fila][columna] = unidad;
        unidad.setPosicion(new Posicion(fila, columna));
        return true;
    }

    public boolean quitar(UnidadCombate unidad) {
        if (unidad == null || unidad == nucleo || !estaEnElMapa(unidad)) {
            return false; // el núcleo no se puede quitar
        }
        Posicion p = unidad.getPosicion();
        celdas[p.getFila()][p.getColumna()] = null;
        unidad.setPosicion(null);
        return true;
    }

    public boolean mover(UnidadCombate unidad, int nuevaFila, int nuevaColumna) {
        if (unidad == null || unidad == nucleo || !estaEnElMapa(unidad)) {
            return false; // el núcleo es fijo
        }
        if (!estaLibre(nuevaFila, nuevaColumna)) {
            return false;
        }
        Posicion vieja = unidad.getPosicion();
        celdas[vieja.getFila()][vieja.getColumna()] = null;
        celdas[nuevaFila][nuevaColumna] = unidad;
        unidad.setPosicion(new Posicion(nuevaFila, nuevaColumna));
        return true;
    }

    // Devuelve las unidades VIVAS que están a 'distancia' casillas o menos
    // del centro (sin contar la que está en el centro).
    public ArrayList<UnidadCombate> unidadesCerca(Posicion centro, int distancia) {
        ArrayList<UnidadCombate> resultado = new ArrayList<>();
        for (int f = centro.getFila() - distancia; f <= centro.getFila() + distancia; f++) {
            for (int c = centro.getColumna() - distancia; c <= centro.getColumna() + distancia; c++) {
                boolean esElCentro = (f == centro.getFila() && c == centro.getColumna());
                UnidadCombate unidad = obtener(f, c); // null si está vacía o fuera del mapa
                if (!esElCentro && unidad != null && unidad.estaViva()) {
                    resultado.add(unidad);
                }
            }
        }
        return resultado;
    }

    // Revisa que la unidad esté realmente en la celda que dice su posición
    private boolean estaEnElMapa(UnidadCombate unidad) {
        Posicion p = unidad.getPosicion();
        return p != null && obtener(p.getFila(), p.getColumna()) == unidad;
    }

    public Nucleo getNucleo() {
        return nucleo;
    }

    public int getTamaño() {
        return TAMAÑO;
    }
}
