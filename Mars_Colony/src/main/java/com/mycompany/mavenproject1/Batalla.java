package com.mycompany.mavenproject1;

import java.util.ArrayList;

/**
 * Dirige una batalla sobre un mapa: coloca las unidades, ejecuta los
 * ciclos (cada unidad juega su turno) y sabe cuándo termina y quién ganó.
 * Por ahora sin hilos (eso va en la etapa 8).
 *
 * @author dylnr
 */
public class Batalla {

    private Mapa mapa;
    private ArrayList<Defensa> defensas;   // colección polimórfica
    private ArrayList<Criatura> criaturas; // colección polimórfica
    private int ciclosJugados;

    public Batalla(Mapa mapa) {
        this.mapa = mapa;
        this.defensas = new ArrayList<>();
        this.criaturas = new ArrayList<>();
        this.ciclosJugados = 0;
    }

    // Coloca una defensa donde el jugador la eligió.
    // Devuelve false si la casilla no es válida (fuera del mapa u ocupada).
    public boolean agregarDefensa(Defensa defensa, int fila, int columna) {
        if (mapa.colocar(defensa, fila, columna)) {
            defensas.add(defensa);
            return true;
        }
        return false;
    }

    // Coloca una criatura en una casilla libre AL AZAR del borde del mapa
    // (fuera del perímetro). Devuelve false si el borde está lleno.
    public boolean agregarCriaturaAlAzar(Criatura criatura) {
        ArrayList<Posicion> libres = casillasLibresDelBorde();
        if (libres.size() == 0) {
            return false;
        }
        // Math.random() da un decimal entre 0 y 1 (sin llegar a 1).
        // Multiplicado por la cantidad de casillas da un índice al azar.
        int indice = (int) (Math.random() * libres.size());
        Posicion elegida = libres.get(indice);
        if (mapa.colocar(criatura, elegida.getFila(), elegida.getColumna())) {
            criaturas.add(criatura);
            return true;
        }
        return false;
    }

    // Todas las casillas vacías de la primera y última fila
    // y de la primera y última columna.
    private ArrayList<Posicion> casillasLibresDelBorde() {
        ArrayList<Posicion> libres = new ArrayList<>();
        int ultimo = Mapa.TAMAÑO - 1;
        for (int f = 0; f < Mapa.TAMAÑO; f++) {
            for (int c = 0; c < Mapa.TAMAÑO; c++) {
                boolean esBorde = (f == 0 || f == ultimo || c == 0 || c == ultimo);
                if (esBorde && mapa.estaLibre(f, c)) {
                    libres.add(new Posicion(f, c));
                }
            }
        }
        return libres;
    }

    // Un ciclo: primero juegan las defensas vivas, después las criaturas
    // vivas, y al final se quitan del mapa las unidades muertas.
    public void ejecutarCiclo() {
        for (Defensa defensa : defensas) {
            if (defensa.estaViva()) {
                defensa.jugarTurno(mapa); // polimorfismo: cada tipo hace lo suyo
            }
        }
        for (Criatura criatura : criaturas) {
            if (criatura.estaViva()) {
                criatura.jugarTurno(mapa);
            }
        }
        quitarMuertos();
        ciclosJugados = ciclosJugados + 1;
    }

    // Quita del mapa a los muertos para liberar sus casillas.
    // NO los quita de las listas: al final se necesita su registro.
    private void quitarMuertos() {
        for (Defensa defensa : defensas) {
            if (!defensa.estaViva() && defensa.getPosicion() != null) {
                mapa.quitar(defensa);
            }
        }
        for (Criatura criatura : criaturas) {
            if (!criatura.estaViva() && criatura.getPosicion() != null) {
                mapa.quitar(criatura);
            }
        }
    }

    // La batalla termina si el núcleo murió o si no queda ninguna criatura viva.
    public boolean haTerminado() {
        if (!mapa.getNucleo().estaViva()) {
            return true;
        }
        for (Criatura criatura : criaturas) {
            if (criatura.estaViva()) {
                return false; // todavía hay al menos una criatura viva
            }
        }
        return true;
    }

    // Ganan las defensas si el núcleo sigue vivo y todas las criaturas murieron.
    public boolean ganaronLasDefensas() {
        return mapa.getNucleo().estaViva() && haTerminado();
    }

    // Juega ciclos hasta que la batalla termine.
    // maxCiclos es un tope de seguridad para no quedarse en un ciclo infinito.
    public void jugar(int maxCiclos) {
        while (!haTerminado() && ciclosJugados < maxCiclos) {
            ejecutarCiclo();
        }
    }

    public Mapa getMapa() {
        return mapa;
    }

    public int getCiclosJugados() {
        return ciclosJugados;
    }

    // Se devuelven copias para que nadie de afuera cambie las listas reales
    public ArrayList<Defensa> getDefensas() {
        return new ArrayList<>(defensas);
    }

    public ArrayList<Criatura> getCriaturas() {
        return new ArrayList<>(criaturas);
    }
}
