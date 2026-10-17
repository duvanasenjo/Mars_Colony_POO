package com.mycompany.mavenproject1;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Progreso del comandante: su nombre, en qué misión va, cuánta
 * capacidad tiene su escuadrón y las PLANTILLAS de defensas y
 * criaturas (los "modelos" que crecen de misión en misión).
 * La capacidad pertenece al escuadrón, no al mapa.
 *
 * @author dylnr
 */
public class Partida implements Serializable {

    public static final int CAPACIDAD_INICIAL = 20;
    public static final int CAPACIDAD_POR_MISION = 5;
    public static final int MISIONES_INICIALES = 10;
    public static final int VIDA_NUCLEO = 100; // decisión de diseño (el enunciado no la define)

    private String nombreComandante;
    private int misionActual;
    private int capacidadTotal;
    private ArrayList<Defensa> tiposDefensas;   // plantillas de defensas del jugador
    private ArrayList<Criatura> tiposCriaturas; // plantillas de criaturas enemigas
    private Batalla batallaActual; // perímetro en preparación o batalla en curso (null si no hay)
    // Perímetro anotado antes de la batalla (misma posición en las dos listas)
    private ArrayList<String> perimetroNombres;
    private ArrayList<Posicion> perimetroPosiciones;

    public Partida(String nombreComandante) {
        this.nombreComandante = nombreComandante;
        this.misionActual = 1;
        this.capacidadTotal = CAPACIDAD_INICIAL;
        this.tiposDefensas = new ArrayList<>();
        this.tiposCriaturas = new ArrayList<>();
        this.batallaActual = null;
        this.perimetroNombres = new ArrayList<>();
        this.perimetroPosiciones = new ArrayList<>();
    }

    // Crea un mapa y una batalla nuevos para la misión actual.
    // Se guarda en la partida para conservar el perímetro al guardar.
    public Batalla prepararBatalla() {
        batallaActual = new Batalla(new Mapa(VIDA_NUCLEO));
        return batallaActual;
    }

    // ----- Perímetro (para repetir o avanzar de misión) -----

    // Antes de la batalla: anota el nombre y la casilla de cada defensa.
    // Hace falta porque las defensas que mueren pierden su posición.
    public void guardarPerimetro() {
        perimetroNombres = new ArrayList<>();
        perimetroPosiciones = new ArrayList<>();
        for (Defensa defensa : batallaActual.getDefensas()) {
            Posicion p = defensa.getPosicion();
            if (p != null) {
                perimetroNombres.add(defensa.getNombre());
                perimetroPosiciones.add(new Posicion(p.getFila(), p.getColumna()));
            }
        }
    }

    // Después de la batalla: crea una batalla nueva y vuelve a poner una
    // COPIA de cada plantilla en su casilla. Si se superó la misión, las
    // copias ya traen el crecimiento. Las que ya no alcancen por capacidad
    // simplemente no se colocan.
    public Batalla reconstruirPerimetro() {
        Batalla nueva = prepararBatalla();
        for (int i = 0; i < perimetroNombres.size(); i++) {
            Defensa plantilla = buscarPlantilla(perimetroNombres.get(i));
            if (plantilla != null) {
                Posicion p = perimetroPosiciones.get(i);
                colocarDefensa(nueva, plantilla.copiar(), p.getFila(), p.getColumna());
            }
        }
        return nueva;
    }

    private Defensa buscarPlantilla(String nombre) {
        for (Defensa tipo : tiposDefensas) {
            if (tipo.getNombre().equals(nombre)) {
                return tipo;
            }
        }
        return null;
    }

    // ----- Plantillas -----

    public void agregarTipoDefensa(Defensa defensa) {
        tiposDefensas.add(defensa);
    }

    public void agregarTipoCriatura(Criatura criatura) {
        tiposCriaturas.add(criatura);
    }

    // Toma del catálogo SOLO las configuraciones activas. Agrega copias
    // para que el catálogo del administrador no cambie cuando las
    // plantillas crezcan en esta partida.
    public void cargarPlantillas(Catalogo catalogo) {
        for (Defensa defensa : catalogo.defensasActivas()) {
            tiposDefensas.add(defensa.copiar());
        }
        for (Criatura criatura : catalogo.criaturasActivas()) {
            tiposCriaturas.add(criatura.copiar());
        }
    }

    // Plantillas de defensa que ya se pueden usar en esta misión
    public ArrayList<Defensa> defensasDisponibles() {
        ArrayList<Defensa> disponibles = new ArrayList<>();
        for (Defensa tipo : tiposDefensas) {
            if (tipo.getMisionMinima() <= misionActual) {
                disponibles.add(tipo);
            }
        }
        return disponibles;
    }

    // ----- Capacidad -----

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

    // Coloca la defensa solo si ya puede aparecer en esta misión,
    // si alcanza la capacidad y si NO es el borde (el borde queda libre
    // para que aparezcan las criaturas). Devuelve false si no se pudo.
    public boolean colocarDefensa(Batalla batalla, Defensa defensa, int fila, int columna) {
        if (batalla.getMapa().esBorde(fila, columna)) {
            return false; // zona de aparición de las criaturas
        }
        if (defensa.getMisionMinima() > misionActual) {
            return false; // todavía no está disponible en esta misión
        }
        if (defensa.getCosto() > capacidadRestante(batalla)) {
            return false; // no alcanza la capacidad
        }
        return batalla.agregarDefensa(defensa, fila, columna);
    }

    // ----- Ejército enemigo -----

    // Crea el ejército de la misión: usa una capacidad igual a la del
    // jugador, solo con criaturas que ya pueden aparecer. Elige al azar,
    // copia la plantilla y la pone en el borde. Devuelve cuántas creó.
    public int generarEjercito(Batalla batalla) {
        int restante = capacidadTotal;
        int creadas = 0;
        while (true) {
            // 1) Plantillas que ya pueden aparecer y caben en lo que queda
            ArrayList<Criatura> posibles = new ArrayList<>();
            for (Criatura tipo : tiposCriaturas) {
                boolean yaAparece = tipo.getMisionMinima() <= misionActual;
                boolean cabe = tipo.getCosto() > 0 && tipo.getCosto() <= restante;
                if (yaAparece && cabe) {
                    posibles.add(tipo);
                }
            }
            if (posibles.size() == 0) {
                return creadas; // ya no cabe ninguna
            }
            // 2) Elegir una al azar, copiarla y ponerla en el borde
            int indice = (int) (Math.random() * posibles.size());
            Criatura nueva = posibles.get(indice).copiar();
            if (!batalla.agregarCriaturaAlAzar(nueva)) {
                return creadas; // el borde está lleno
            }
            restante = restante - nueva.getCosto();
            creadas = creadas + 1;
        }
    }

    // ----- Misiones -----

    // Se llama cuando el jugador gana la misión y decide avanzar.
    // (Para repetir la misión simplemente NO se llama.)
    // Todas las plantillas crecen al azar entre 5% y 20% y queda registrado.
    public void superarMision() {
        misionActual = misionActual + 1;
        capacidadTotal = capacidadTotal + CAPACIDAD_POR_MISION;
        for (Defensa tipo : tiposDefensas) {
            tipo.mejorarAlAzar(misionActual);
        }
        for (Criatura tipo : tiposCriaturas) {
            tipo.mejorarAlAzar(misionActual);
        }
    }

    // true cuando ya pasó la misión 10: el jugador puede terminar
    // la campaña o seguir con misiones nuevas.
    public boolean terminoMisionesIniciales() {
        return misionActual > MISIONES_INICIALES;
    }

    // ----- Getters -----

    public String getNombreComandante() {
        return nombreComandante;
    }

    public int getMisionActual() {
        return misionActual;
    }

    public int getCapacidadTotal() {
        return capacidadTotal;
    }

    public Batalla getBatallaActual() {
        return batallaActual;
    }

    // Copias para que nadie cambie las listas reales
    public ArrayList<Defensa> getTiposDefensas() {
        return new ArrayList<>(tiposDefensas);
    }

    public ArrayList<Criatura> getTiposCriaturas() {
        return new ArrayList<>(tiposCriaturas);
    }
}
