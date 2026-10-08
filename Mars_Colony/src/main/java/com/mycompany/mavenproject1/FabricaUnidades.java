package com.mycompany.mavenproject1;

/**
 * FÁBRICA de unidades: recibe el TIPO elegido (por ejemplo "Dron") y sus
 * datos, y crea un objeto de la SUBCLASE correcta.
 *
 * Es la "creación polimórfica" que pide el enunciado: quien la usa solo
 * recibe una Defensa o una Criatura, sin saber qué subclase es; la
 * fábrica es el único lugar donde se decide con qué "new" se crea.
 *
 * Los nombres de los tipos están en constantes para usarlos igual en
 * todas partes (cada subclase devuelve el suyo en getTipo()).
 *
 * @author dylnr
 */
public class FabricaUnidades {

    // ---------- Tipos de defensa ----------
    public static final String CONTACTO = "Contacto";
    public static final String ALCANCE_MEDIO = "Alcance medio";
    public static final String ATAQUE_MULTIPLE = "Ataque múltiple";
    public static final String IMPACTO = "Impacto";
    public static final String DRON = "Dron";
    public static final String BARRERA = "Barrera";

    // ---------- Tipos de criatura ----------
    public static final String ACECHADOR = "Acechador";
    public static final String ESCUPIDOR = "Escupidor";
    public static final String VOLADOR = "Volador";
    public static final String ENJAMBRE = "Enjambre";
    public static final String DEMOLEDOR = "Demoledor";

    public static final String[] TIPOS_DEFENSA = {CONTACTO, ALCANCE_MEDIO, ATAQUE_MULTIPLE, IMPACTO, DRON, BARRERA};
    public static final String[] TIPOS_CRIATURA = {ACECHADOR, ESCUPIDOR, VOLADOR, ENJAMBRE, DEMOLEDOR};

    // ¿El tipo es de defensa? (si no, es de criatura)
    public boolean esDefensa(String tipo) {
        for (String t : TIPOS_DEFENSA) {
            if (t.equals(tipo)) {
                return true;
            }
        }
        return false;
    }

    // Crea la defensa del tipo indicado (nivel 1). Devuelve null si el tipo no es de defensa.
    // Los datos que un tipo no usa simplemente se ignoran.
    public Defensa crearDefensa(String tipo, String nombre, int vida, int daño, int costo,
            double frecuencia, int alcance, int radio, int cantidad) {
        if (tipo.equals(CONTACTO)) {
            return new Contacto(nombre, vida, daño, 1, costo, frecuencia);
        } else if (tipo.equals(ALCANCE_MEDIO)) {
            return new Alcance_medio(nombre, vida, daño, 1, costo, frecuencia, alcance);
        } else if (tipo.equals(ATAQUE_MULTIPLE)) {
            return new Ataque_multiple(nombre, vida, daño, 1, costo, frecuencia, alcance, cantidad);
        } else if (tipo.equals(IMPACTO)) {
            return new Impacto(nombre, vida, daño, 1, costo, frecuencia, radio);
        } else if (tipo.equals(DRON)) {
            return new Dron(nombre, vida, daño, 1, costo, frecuencia, alcance);
        } else if (tipo.equals(BARRERA)) {
            return new Barrera(nombre, vida, 1, costo);
        }
        return null;
    }

    // Crea la criatura del tipo indicado (nivel 1). Devuelve null si el tipo no es de criatura.
    public Criatura crearCriatura(String tipo, String nombre, int vida, int daño, int costo,
            double frecuencia, int alcance, int radio, int cantidad) {
        if (tipo.equals(ACECHADOR)) {
            return new Acechador(nombre, vida, daño, 1, costo, frecuencia);
        } else if (tipo.equals(ESCUPIDOR)) {
            return new Escupidor(nombre, vida, daño, 1, costo, frecuencia, alcance);
        } else if (tipo.equals(VOLADOR)) {
            return new Volador(nombre, vida, daño, 1, costo, frecuencia);
        } else if (tipo.equals(ENJAMBRE)) {
            return new Enjambre(nombre, vida, daño, 1, costo, frecuencia, alcance, cantidad);
        } else if (tipo.equals(DEMOLEDOR)) {
            return new Demoledor(nombre, vida, daño, 1, costo, frecuencia, radio);
        }
        return null;
    }

    // ---------- Qué datos usa cada tipo (para activar o desactivar campos) ----------

    public boolean usaDaño(String tipo) {
        return !tipo.equals(BARRERA);
    }

    public boolean usaFrecuencia(String tipo) {
        return !tipo.equals(BARRERA);
    }

    public boolean usaAlcance(String tipo) {
        return tipo.equals(ALCANCE_MEDIO) || tipo.equals(ATAQUE_MULTIPLE) || tipo.equals(DRON)
                || tipo.equals(ESCUPIDOR) || tipo.equals(ENJAMBRE);
    }

    public boolean usaRadio(String tipo) {
        return tipo.equals(IMPACTO) || tipo.equals(DEMOLEDOR);
    }

    // Ataque múltiple: cuántos objetivos. Enjambre: cuántos ataques.
    public boolean usaCantidad(String tipo) {
        return tipo.equals(ATAQUE_MULTIPLE) || tipo.equals(ENJAMBRE);
    }
}
