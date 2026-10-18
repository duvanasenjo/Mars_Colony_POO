package com.mycompany.mavenproject1;

import com.mycompany.mavenproject1.hilos.HiloMapa;
import com.mycompany.mavenproject1.hilos.HiloUnidad;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Dirige una batalla sobre un mapa: coloca las unidades, la hace correr
 * y sabe cuándo termina y quién ganó.
 *
 * Hay dos formas de jugarla:
 *  - CON HILOS (la del juego): iniciar() arranca un HiloUnidad por cada
 *    unidad y un HiloMapa. Las unidades piden turno en la ColaTurnos y el
 *    HiloMapa los ejecuta en orden.
 *  - SIN HILOS (para pruebas): jugar() / ejecutarCiclo(), todas en orden.
 *
 * @author dylnr
 */
public class Batalla implements Serializable {

    // Número de versión fijo para que las partidas guardadas sigan abriendo
    private static final long serialVersionUID = -3056286848464363793L;

    public static final long PAUSA_BASE = 2000;      // ms entre turnos con frecuencia 1.0

    private Mapa mapa;
    private ArrayList<Defensa> defensas;   // colección polimórfica
    private ArrayList<Criatura> criaturas; // colección polimórfica
    private int ciclosJugados;
    private int ultimoId;            // último número de identificador que se dio

    // ----- Para la batalla con hilos -----
    private ColaTurnos colaTurnos;   // fila de unidades que pidieron turno
    private boolean enCurso;         // true mientras los hilos deben seguir
    private long inicio;             // hora en que empezó (milisegundos)
    private long fin;                // hora en que terminó (0 si sigue)

    public Batalla(Mapa mapa) {
        this.mapa = mapa;
        this.defensas = new ArrayList<>();
        this.criaturas = new ArrayList<>();
        this.ciclosJugados = 0;
        this.ultimoId = 0;
        this.colaTurnos = new ColaTurnos();
        this.enCurso = false;
        this.inicio = 0;
        this.fin = 0;
    }

    // ----- Batalla con hilos -----

    // Arranca un hilo por cada defensa ACTIVA y criatura viva, y el hilo del mapa.
    // start() pone a correr el run() de cada hilo al mismo tiempo.
    public void iniciar() {
        if (enCurso) {
            return;   // ya estaba corriendo
        }
        colaTurnos = new ColaTurnos();
        enCurso = true;
        // System.currentTimeMillis(): la hora actual en milisegundos
        inicio = System.currentTimeMillis();
        fin = 0;
        for (Defensa defensa : defensas) {
            // La barrera no ataca ni se mueve: no necesita hilo (solo bloquea y recibe daño)
            if (defensa.estaViva() && !defensa.esBarrera()) {
                HiloUnidad hilo = new HiloUnidad(defensa, this);
                hilo.start();
            }
        }
        for (Criatura criatura : criaturas) {
            if (criatura.estaViva()) {
                HiloUnidad hilo = new HiloUnidad(criatura, this);
                hilo.start();
            }
        }
        HiloMapa hiloMapa = new HiloMapa(this);
        hiloMapa.start();
    }

    // Avisa a todos los hilos que terminen (cada uno sale de su while).
    public void detener() {
        terminar();
    }

    // La llama el HiloMapa después de cada turno.
    // Termina SOLO si el núcleo murió o no queda ninguna criatura viva (como dice el enunciado).
    public void revisarFin() {
        if (haTerminado()) {
            terminar();
        }
    }

    // Apaga la batalla y congela el reloj.
    private void terminar() {
        if (enCurso) {
            fin = System.currentTimeMillis();
        }
        enCurso = false;
    }

    // Segundos que lleva la batalla (0 si no ha empezado).
    public int getSegundos() {
        if (inicio == 0) {
            return 0;
        }
        if (fin != 0) {
            return (int) ((fin - inicio) / 1000);   // ya terminó: reloj congelado
        }
        return (int) ((System.currentTimeMillis() - inicio) / 1000);
    }

    // Coloca una defensa donde el jugador la eligió.
    // Devuelve false si la casilla no es válida (fuera del mapa u ocupada).
    public boolean agregarDefensa(Defensa defensa, int fila, int columna) {
        if (mapa.colocar(defensa, fila, columna)) {
            defensas.add(defensa);
            darId(defensa);
            return true;
        }
        return false;
    }

    // Cada unidad que entra a la batalla recibe un número distinto (1, 2, 3...)
    private void darId(UnidadCombate unidad) {
        ultimoId = ultimoId + 1;
        unidad.setId(ultimoId);
    }

    // Quita una defensa durante la preparación (el jugador se arrepintió).
    // La saca del mapa y de la lista, así su costo vuelve a quedar libre.
    // Devuelve false si esa defensa no está en esta batalla.
    public boolean quitarDefensa(Defensa defensa) {
        if (!defensas.contains(defensa)) {
            return false;
        }
        mapa.quitar(defensa);
        defensas.remove(defensa);
        return true;
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
            darId(criatura);
            return true;
        }
        return false;
    }

    // Todas las casillas vacías de la primera y última fila
    // y de la primera y última columna.
    private ArrayList<Posicion> casillasLibresDelBorde() {
        ArrayList<Posicion> libres = new ArrayList<>();
        for (int f = 0; f < Mapa.TAMAÑO; f++) {
            for (int c = 0; c < Mapa.TAMAÑO; c++) {
                if (mapa.esBorde(f, c) && mapa.estaLibre(f, c)) {
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
    // Es public porque también lo llama el HiloMapa.
    public void quitarMuertos() {
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

    // Ganan las defensas si la batalla terminó y el núcleo sigue vivo
    // (o sea, murieron todas las criaturas).
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

    public ColaTurnos getColaTurnos() {
        return colaTurnos;
    }

    public boolean isEnCurso() {
        return enCurso;
    }

    // Se devuelven copias para que nadie de afuera cambie las listas reales
    public ArrayList<Defensa> getDefensas() {
        return new ArrayList<>(defensas);
    }

    public ArrayList<Criatura> getCriaturas() {
        return new ArrayList<>(criaturas);
    }
}
