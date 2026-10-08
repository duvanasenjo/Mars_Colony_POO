package com.mycompany.mavenproject1;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author dylnr
 */
public abstract class UnidadCombate implements Serializable {

    private String nombre;
    private int vidaMaxima;
    private int vidaActual;
    private int daño;
    private int nivel;
    private int costo;         // puntos de capacidad que cuesta
    private int alcance;       // a cuántas casillas puede atacar
    private int radio;         // área de daño de una explosión (0 = no explota)
    private double frecuencia; // ataques por segundo (0.5 = uno cada 2 segundos)
    private Posicion posicion; // null hasta que el Mapa la coloque
    private RegistroCombate registro; // historial de combate de esta unidad
    private int misionMinima;          // desde qué misión puede aparecer (empieza en 1)
    private ArrayList<String> historialMejoras; // registro del crecimiento por misión
    private boolean activo;                // si está desactivada, el juego no la usa
    private String rutaImagenNormal;       // imágenes que pide el enunciado
    private String rutaImagenMovimiento;
    private String rutaImagenAtaque;

    public UnidadCombate(String nombre, int vidaMaxima, int daño, int nivel,
                         int costo, int alcance, int radio, double frecuencia) {
        // Validaciones: una unidad mal configurada no se puede crear
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (vidaMaxima <= 0) {
            throw new IllegalArgumentException("La vida máxima debe ser mayor que 0");
        }
        if (nivel < 1) {
            throw new IllegalArgumentException("El nivel debe ser 1 o mayor");
        }
        if (daño < 0 || costo < 0 || alcance < 0 || radio < 0 || frecuencia < 0) {
            throw new IllegalArgumentException(
                    "Daño, costo, alcance, radio y frecuencia no pueden ser negativos");
        }
        this.nombre = nombre;
        this.vidaMaxima = vidaMaxima;
        this.vidaActual = vidaMaxima;
        this.daño = daño;
        this.nivel = nivel;
        this.costo = costo;
        this.alcance = alcance;
        this.radio = radio;
        this.frecuencia = frecuencia;
        this.registro = new RegistroCombate();
        this.misionMinima = 1;
        this.historialMejoras = new ArrayList<>();
        this.activo = true;
        this.rutaImagenNormal = "";
        this.rutaImagenMovimiento = "";
        this.rutaImagenAtaque = "";
    }

    // Pasa a una copia los datos que no van en el constructor.
    // Lo usan los copiar() de las 11 clases concretas.
    public void copiarDatosA(UnidadCombate copia) {
        copia.setMisionMinima(misionMinima);
        copia.setActivo(activo);
        copia.setRutaImagenNormal(rutaImagenNormal);
        copia.setRutaImagenMovimiento(rutaImagenMovimiento);
        copia.setRutaImagenAtaque(rutaImagenAtaque);
    }

    // Por defecto las unidades son terrestres. Volador lo sobrescribe.
    public boolean esAerea() {
        return false;
    }

    // ¿De qué bando es? Por defecto no es criatura. Criatura lo sobrescribe.
    public boolean esCriatura() {
        return false;
    }

    // ¿Es una barrera? Por defecto no. Barrera lo sobrescribe.
    public boolean esBarrera() {
        return false;
    }

    // ¿Puedo atacar a 'otra'? Por defecto no (Barrera y Núcleo no atacan).
    // Defensa y Criatura lo sobrescriben con sus reglas.
    public boolean puedeAtacarA(UnidadCombate otra) {
        return false;
    }

    // Busca en el mapa las unidades a 'distancia' casillas o menos
    // que esta unidad puede atacar.
    public ArrayList<UnidadCombate> buscarObjetivos(Mapa mapa, int distancia) {
        ArrayList<UnidadCombate> objetivos = new ArrayList<>();
        if (!estaViva() || posicion == null) {
            return objetivos; // muerta o fuera del mapa: no ataca a nadie
        }
        ArrayList<UnidadCombate> cercanas = mapa.unidadesCerca(posicion, distancia);
        for (UnidadCombate otra : cercanas) {
            if (puedeAtacarA(otra)) {
                objetivos.add(otra);
            }
        }
        return objetivos;
    }

    // Devuelve la unidad de la lista que está más cerca (null si la lista está vacía)
    public UnidadCombate elegirMasCercano(ArrayList<UnidadCombate> lista) {
        UnidadCombate elegido = null;
        int menorDistancia = 0;
        for (UnidadCombate otra : lista) {
            int distancia = posicion.distanciaA(otra.getPosicion());
            if (elegido == null || distancia < menorDistancia) {
                elegido = otra;
                menorDistancia = distancia;
            }
        }
        return elegido;
    }

    // Ataque simple: busca objetivos a 'distancia' casillas o menos,
    // elige el más cercano y lo golpea con su daño.
    public void atacarAlMasCercano(Mapa mapa, int distancia) {
        ArrayList<UnidadCombate> objetivos = buscarObjetivos(mapa, distancia);
        UnidadCombate objetivo = elegirMasCercano(objetivos);
        if (objetivo != null) {
            golpear(objetivo, daño);
        }
    }

    // ¿Hacia quién me muevo? Si ya tengo un objetivo dentro de mi alcance,
    // no necesito moverme (devuelve null). Si no, devuelve el objetivo
    // más cercano de todo el mapa (o null si no hay ninguno).
    public UnidadCombate buscarDestino(Mapa mapa) {
        if (buscarObjetivos(mapa, alcance).size() > 0) {
            return null; // ya puedo atacar desde aquí
        }
        return elegirMasCercano(buscarObjetivos(mapa, Mapa.TAMAÑO));
    }

    // Para ir de 'desde' hasta 'hacia': +1, -1 o 0
    public int direccion(int desde, int hacia) {
        if (hacia > desde) {
            return 1;
        }
        if (hacia < desde) {
            return -1;
        }
        return 0;
    }

    // Da UN paso (una casilla) hacia 'destino'. Si el camino directo
    // está ocupado, intenta rodear. Devuelve true si logró moverse.
    public boolean darPasoHacia(Mapa mapa, Posicion destino) {
        int f = posicion.getFila();
        int c = posicion.getColumna();
        int df = direccion(f, destino.getFila());
        int dc = direccion(c, destino.getColumna());
        if (df == 0 && dc == 0) {
            return false; // ya está en el destino
        }
        // 1) Camino directo
        if (mapa.mover(this, f + df, c + dc)) {
            return true;
        }
        // 2) Si iba en diagonal: probar solo la fila o solo la columna
        if (df != 0 && dc != 0) {
            if (mapa.mover(this, f + df, c)) {
                return true;
            }
            if (mapa.mover(this, f, c + dc)) {
                return true;
            }
        }
        // 3) Si iba recto en columnas: probar las diagonales de enfrente
        if (df == 0) {
            if (mapa.mover(this, f + 1, c + dc)) {
                return true;
            }
            if (mapa.mover(this, f - 1, c + dc)) {
                return true;
            }
        }
        // 4) Si iba recto en filas: probar las diagonales de enfrente
        if (dc == 0) {
            if (mapa.mover(this, f + df, c + 1)) {
                return true;
            }
            if (mapa.mover(this, f + df, c - 1)) {
                return true;
            }
        }
        return false; // todo ocupado: se queda quieta
    }

    // Lo que hace la unidad en cada ciclo de la batalla.
    // Por defecto NADA (así quedan Barrera y Núcleo).
    // Las que atacan o se mueven lo sobrescriben.
    public void jugarTurno(Mapa mapa) {
    }

    // Deja la vida en 0. Lo usan Impacto y Demoledor después de explotar.
    public void destruir() {
        vidaActual = 0;
    }

    // Resta vida; nunca baja de 0
    public void recibirDaño(int cantidad) {
        vidaActual = Math.max(0, vidaActual - cantidad);
    }

    // La forma de hacer daño: le quita vida al objetivo
    // y lo anota en el registro de las dos unidades.
    public void golpear(UnidadCombate objetivo, int cantidad) {
        if (!objetivo.estaViva()) {
            return; // a un objetivo muerto no se le hace nada
        }
        // Daño real: no puede ser mayor que la vida que le queda
        int dañoReal = cantidad;
        if (dañoReal > objetivo.getVidaActual()) {
            dañoReal = objetivo.getVidaActual();
        }
        objetivo.recibirDaño(dañoReal);
        this.registro.registrarAtaque(objetivo, dañoReal);
        objetivo.getRegistro().registrarRecibido(this, dañoReal);
    }

    public boolean estaViva() {
        return vidaActual > 0;
    }

    // Porcentajes de 0.05 a 0.20 (5% a 20%). Igual para todas las unidades.
    public void mejorar(double porcentajeVida, double porcentajeDaño) {
        int nuevaVida = (int) Math.round(vidaMaxima * (1 + porcentajeVida));
        // Con números pequeños el redondeo puede "comerse" el aumento
        // (ej.: 2 + 5% = 2.1 -> 2). En ese caso sube al menos 1.
        if (porcentajeVida > 0 && nuevaVida == vidaMaxima) {
            nuevaVida = vidaMaxima + 1;
        }
        int nuevoDaño = (int) Math.round(daño * (1 + porcentajeDaño));
        if (porcentajeDaño > 0 && daño > 0 && nuevoDaño == daño) {
            nuevoDaño = daño + 1; // si el daño es 0 (Barrera, Núcleo) se queda en 0
        }
        vidaMaxima = nuevaVida;
        daño = nuevoDaño;
        vidaActual = vidaMaxima;
        nivel++;
    }

    // Crecimiento al avanzar de misión: vida y daño suben cada uno
    // un porcentaje AL AZAR entre 5% y 20% (por separado) y queda registrado.
    public void mejorarAlAzar(int numeroMision) {
        // Math.random() da un decimal entre 0 y 1, así que 0.05 + (0 a 0.15) = 5% a 20%
        double porcentajeVida = 0.05 + Math.random() * 0.15;
        double porcentajeDaño = 0.05 + Math.random() * 0.15;
        int vidaAntes = vidaMaxima;
        int dañoAntes = daño;

        mejorar(porcentajeVida, porcentajeDaño);

        int pv = (int) Math.round(porcentajeVida * 100);
        int pd = (int) Math.round(porcentajeDaño * 100);
        historialMejoras.add("Misión " + numeroMision + ": vida " + vidaAntes + " -> " + vidaMaxima
                + " (+" + pv + "%), daño " + dañoAntes + " -> " + daño + " (+" + pd + "%)");
    }

    // Nombre del tipo concreto ("Dron", "Acechador"...). Cada subclase dice
    // el suyo (polimorfismo); lo usan la fábrica y el programa de administración.
    public abstract String getTipo();

    // Cantidad de objetivos o de ataques por turno. Solo Ataque_multiple y
    // Enjambre la usan (la sobrescriben); para los demás tipos es 0.
    public int getCantidad() {
        return 0;
    }

    public String getNombre() {
        return nombre;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public int getVidaActual() {
        return vidaActual;
    }

    public int getDaño() {
        return daño;
    }

    public int getNivel() {
        return nivel;
    }

    public int getCosto() {
        return costo;
    }

    public int getAlcance() {
        return alcance;
    }

    public int getRadio() {
        return radio;
    }

    public double getFrecuencia() {
        return frecuencia;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    // La usa el Mapa al colocar o mover la unidad
    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    public RegistroCombate getRegistro() {
        return registro;
    }

    public int getMisionMinima() {
        return misionMinima;
    }

    // Si viene un número menor que 1, se deja en 1
    public void setMisionMinima(int misionMinima) {
        if (misionMinima < 1) {
            misionMinima = 1;
        }
        this.misionMinima = misionMinima;
    }

    // Se devuelve una copia para que nadie cambie el historial real
    public ArrayList<String> getHistorialMejoras() {
        return new ArrayList<>(historialMejoras);
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getRutaImagenNormal() {
        return rutaImagenNormal;
    }

    // Si viene null se guarda "" (sin imagen)
    public void setRutaImagenNormal(String ruta) {
        if (ruta == null) {
            ruta = "";
        }
        this.rutaImagenNormal = ruta;
    }

    public String getRutaImagenMovimiento() {
        return rutaImagenMovimiento;
    }

    public void setRutaImagenMovimiento(String ruta) {
        if (ruta == null) {
            ruta = "";
        }
        this.rutaImagenMovimiento = ruta;
    }

    public String getRutaImagenAtaque() {
        return rutaImagenAtaque;
    }

    public void setRutaImagenAtaque(String ruta) {
        if (ruta == null) {
            ruta = "";
        }
        this.rutaImagenAtaque = ruta;
    }
}
