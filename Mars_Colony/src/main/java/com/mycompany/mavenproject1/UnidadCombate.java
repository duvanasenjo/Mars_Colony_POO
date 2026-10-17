package com.mycompany.mavenproject1;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author dylnr
 */
public abstract class UnidadCombate implements Serializable {

    // "Número de versión" de la clase para la serialización. Se deja FIJO
    // para que las partidas y el catálogo guardados antes de agregar
    // atributos nuevos se puedan seguir abriendo.
    private static final long serialVersionUID = 8766806569075243070L;

    // Estado que se dibuja en la batalla (cada uno tiene su imagen)
    public static final String NORMAL = "normal";
    public static final String MOVIMIENTO = "movimiento";
    public static final String ATAQUE = "ataque";
    public static final long DURACION_ESTADO = 1000; // ms que se ve la imagen de movimiento o ataque

    private String nombre;
    private int vidaMaxima;
    private int vidaActual;
    private int daño;
    private int nivel;
    private int costo;         // puntos de capacidad que cuesta
    private int alcance;       // a cuántas casillas puede atacar
    private double frecuencia; // ataques por segundo (0.5 = uno cada 2 segundos)
    private Posicion posicion; // null hasta que el Mapa la coloque
    private Posicion posicionFinal; // última casilla donde estuvo (se conserva aunque muera)
    private String estado;          // NORMAL, MOVIMIENTO o ATAQUE
    private long momentoEstado;     // hora (ms) en que cambió el estado
    private long momentoGolpe;      // hora (ms) del último golpe recibido (0 = nunca)
    private RegistroCombate registro; // historial de combate de esta unidad
    private int misionMinima;          // desde qué misión puede aparecer (empieza en 1)
    private ArrayList<String> historialMejoras; // registro del crecimiento por misión
    private boolean activo;                // si está desactivada, el juego no la usa
    private String rutaImagenNormal;       // imágenes que pide el enunciado
    private String rutaImagenMovimiento;
    private String rutaImagenAtaque;

    public UnidadCombate(String nombre, int vidaMaxima, int daño, int nivel,
                         int costo, int alcance, double frecuencia) {
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
        if (daño < 0 || costo < 0 || alcance < 0 || frecuencia < 0) {
            throw new IllegalArgumentException(
                    "Daño, costo, alcance y frecuencia no pueden ser negativos");
        }
        this.nombre = nombre;
        this.vidaMaxima = vidaMaxima;
        this.vidaActual = vidaMaxima;
        this.daño = daño;
        this.nivel = nivel;
        this.costo = costo;
        this.alcance = alcance;
        this.frecuencia = frecuencia;
        this.registro = new RegistroCombate();
        this.misionMinima = 1;
        this.historialMejoras = new ArrayList<>();
        this.activo = true;
        this.rutaImagenNormal = "";
        this.rutaImagenMovimiento = "";
        this.rutaImagenAtaque = "";
        this.posicionFinal = null;
        this.estado = NORMAL;
        this.momentoEstado = 0;
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
        if (cantidad > 0) {
            momentoGolpe = System.currentTimeMillis();   // para que la interfaz la "encienda" en rojo
        }
    }

    // true durante DURACION_ESTADO ms después de recibir un golpe
    public boolean recibioGolpeRecien() {
        return System.currentTimeMillis() - momentoGolpe < DURACION_ESTADO;
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
        cambiarEstado(ATAQUE);   // para que la interfaz muestre la imagen de ataque
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

    public double getFrecuencia() {
        return frecuencia;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    // La usa el Mapa al colocar o mover la unidad.
    // Si es una casilla (no null), también queda anotada como posición final.
    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
        if (posicion != null) {
            this.posicionFinal = posicion;
        }
    }

    // Última casilla donde estuvo; sirve para el registro aunque ya esté muerta
    public Posicion getPosicionFinal() {
        if (posicionFinal == null) {
            return posicion;   // partidas guardadas antes de este cambio
        }
        return posicionFinal;
    }

    // ----- Estado para las imágenes (normal, movimiento, ataque) -----

    // Lo llaman golpear() (ATAQUE) y el Mapa al mover (MOVIMIENTO).
    public void cambiarEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
        this.momentoEstado = System.currentTimeMillis();
    }

    // Estado que se debe dibujar AHORA: el de movimiento o ataque se ve
    // solo DURACION_ESTADO milisegundos; después vuelve a NORMAL.
    public String getEstadoActual() {
        if (estado == null) {
            return NORMAL;   // partidas guardadas antes de este cambio
        }
        long pasaron = System.currentTimeMillis() - momentoEstado;
        if (pasaron > DURACION_ESTADO) {
            return NORMAL;
        }
        return estado;
    }

    // Ruta de la imagen que corresponde al estado actual.
    // Si esa imagen no está configurada, usa la normal.
    public String getRutaImagenActual() {
        String estadoActual = getEstadoActual();
        if (estadoActual.equals(ATAQUE) && !rutaImagenAtaque.equals("")) {
            return rutaImagenAtaque;
        }
        if (estadoActual.equals(MOVIMIENTO) && !rutaImagenMovimiento.equals("")) {
            return rutaImagenMovimiento;
        }
        return rutaImagenNormal;
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
