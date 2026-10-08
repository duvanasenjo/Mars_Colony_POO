package com.mycompany.mavenproject1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Guarda, carga y lista partidas en archivos (serialización).
 * Cada partida queda en "partidas/<nombre del comandante>.dat".
 * Si algo falla al leer o escribir, se atrapa el error (try/catch)
 * y se devuelve false o null en vez de que el programa se caiga.
 *
 * @author dylnr
 */
public class ArchivoPartidas {

    public static final String CARPETA = "partidas";
    public static final String EXTENSION = ".dat";

    // Guarda la partida completa (con su batalla, mapa y unidades).
    // Si el comandante ya tenía archivo, lo actualiza.
    public boolean guardar(Partida partida) {
        try {
            File carpeta = new File(CARPETA);
            if (!carpeta.exists()) {
                carpeta.mkdirs(); // crea la carpeta si no existe
            }
            File archivo = new File(CARPETA, nombreArchivo(partida.getNombreComandante()));
            ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(archivo));
            salida.writeObject(partida); // guarda el objeto completo
            salida.close();
            return true;
        } catch (Exception e) {
            System.out.println("No se pudo guardar la partida: " + e.getMessage());
            return false;
        }
    }

    // Lee la partida tal como se guardó. Devuelve null si no existe
    // o si el archivo está dañado.
    public Partida cargar(String nombreComandante) {
        try {
            File archivo = new File(CARPETA, nombreArchivo(nombreComandante));
            ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo));
            Partida partida = (Partida) entrada.readObject(); // se convierte a Partida
            entrada.close();
            return partida;
        } catch (Exception e) {
            System.out.println("No se pudo cargar la partida: " + e.getMessage());
            return null;
        }
    }

    // Nombres de los comandantes que tienen una partida guardada
    public ArrayList<String> listar() {
        ArrayList<String> nombres = new ArrayList<>();
        File carpeta = new File(CARPETA);
        File[] archivos = carpeta.listFiles();
        if (archivos == null) {
            return nombres; // la carpeta todavía no existe
        }
        for (File archivo : archivos) {
            String nombre = archivo.getName();
            if (nombre.endsWith(EXTENSION)) {
                // quita la extensión ".dat" del final
                nombres.add(nombre.substring(0, nombre.length() - EXTENSION.length()));
            }
        }
        return nombres;
    }

    // true si ya hay una partida con ese nombre (para que el nombre sea único)
    public boolean existeComandante(String nombreComandante) {
        File archivo = new File(CARPETA, nombreArchivo(nombreComandante));
        return archivo.exists();
    }

    // Cambia por "_" los caracteres que Windows no permite en un nombre de archivo
    private String nombreArchivo(String nombreComandante) {
        String prohibidos = "\\/:*?\"<>|";
        String resultado = "";
        for (int i = 0; i < nombreComandante.length(); i++) {
            char letra = nombreComandante.charAt(i);
            if (prohibidos.indexOf(letra) >= 0) {
                resultado = resultado + "_";
            } else {
                resultado = resultado + letra;
            }
        }
        return resultado + EXTENSION;
    }
}
