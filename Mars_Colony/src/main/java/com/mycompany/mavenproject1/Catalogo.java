package com.mycompany.mavenproject1;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Catálogo de configuraciones de defensas y criaturas.
 * Lo administra el programa de administración (crear, modificar,
 * consultar y desactivar) y el juego toma solo las activas.
 * El nombre identifica a cada configuración (no se repite).
 *
 * @author dylnr
 */
public class Catalogo implements Serializable {

    private ArrayList<Defensa> defensas;
    private ArrayList<Criatura> criaturas;

    public Catalogo() {
        defensas = new ArrayList<>();
        criaturas = new ArrayList<>();
    }

    // ----- Crear -----

    // Devuelve false si ya existe una configuración con ese nombre
    public boolean agregarDefensa(Defensa defensa) {
        if (existeNombre(defensa.getNombre())) {
            return false;
        }
        defensas.add(defensa);
        return true;
    }

    public boolean agregarCriatura(Criatura criatura) {
        if (existeNombre(criatura.getNombre())) {
            return false;
        }
        criaturas.add(criatura);
        return true;
    }

    // ----- Consultar -----

    public Defensa buscarDefensa(String nombre) {
        for (Defensa defensa : defensas) {
            if (defensa.getNombre().equals(nombre)) {
                return defensa;
            }
        }
        return null;
    }

    public Criatura buscarCriatura(String nombre) {
        for (Criatura criatura : criaturas) {
            if (criatura.getNombre().equals(nombre)) {
                return criatura;
            }
        }
        return null;
    }

    public boolean existeNombre(String nombre) {
        return buscarDefensa(nombre) != null || buscarCriatura(nombre) != null;
    }

    // ----- Modificar -----

    // Reemplaza la configuración 'nombre' por 'nueva' en el mismo lugar.
    // Devuelve false si no existe, o si el nombre nuevo ya lo usa otra.
    public boolean modificarDefensa(String nombre, Defensa nueva) {
        Defensa vieja = buscarDefensa(nombre);
        if (vieja == null) {
            return false;
        }
        if (!nueva.getNombre().equals(nombre) && existeNombre(nueva.getNombre())) {
            return false;
        }
        int indice = defensas.indexOf(vieja);
        defensas.set(indice, nueva);
        return true;
    }

    public boolean modificarCriatura(String nombre, Criatura nueva) {
        Criatura vieja = buscarCriatura(nombre);
        if (vieja == null) {
            return false;
        }
        if (!nueva.getNombre().equals(nombre) && existeNombre(nueva.getNombre())) {
            return false;
        }
        int indice = criaturas.indexOf(vieja);
        criaturas.set(indice, nueva);
        return true;
    }

    // ----- Desactivar / activar (no se borra) -----

    public boolean desactivar(String nombre) {
        return cambiarActivo(nombre, false);
    }

    public boolean activar(String nombre) {
        return cambiarActivo(nombre, true);
    }

    private boolean cambiarActivo(String nombre, boolean activo) {
        Defensa defensa = buscarDefensa(nombre);
        if (defensa != null) {
            defensa.setActivo(activo);
            return true;
        }
        Criatura criatura = buscarCriatura(nombre);
        if (criatura != null) {
            criatura.setActivo(activo);
            return true;
        }
        return false; // no existe
    }

    // ----- Lo que usa el juego -----

    public ArrayList<Defensa> defensasActivas() {
        ArrayList<Defensa> activas = new ArrayList<>();
        for (Defensa defensa : defensas) {
            if (defensa.isActivo()) {
                activas.add(defensa);
            }
        }
        return activas;
    }

    public ArrayList<Criatura> criaturasActivas() {
        ArrayList<Criatura> activas = new ArrayList<>();
        for (Criatura criatura : criaturas) {
            if (criatura.isActivo()) {
                activas.add(criatura);
            }
        }
        return activas;
    }

    // Copias para que nadie cambie las listas reales
    public ArrayList<Defensa> getDefensas() {
        return new ArrayList<>(defensas);
    }

    public ArrayList<Criatura> getCriaturas() {
        return new ArrayList<>(criaturas);
    }
}
