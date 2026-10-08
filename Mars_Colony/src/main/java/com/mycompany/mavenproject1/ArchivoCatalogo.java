package com.mycompany.mavenproject1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Guarda y carga el catálogo de configuraciones (serialización).
 * Lo escribe el programa de administración y lo lee el juego.
 *
 * @author dylnr
 */
public class ArchivoCatalogo {

    public static final String ARCHIVO = "catalogo.dat";

    public boolean guardar(Catalogo catalogo) {
        try {
            ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ARCHIVO));
            salida.writeObject(catalogo);
            salida.close();
            return true;
        } catch (Exception e) {
            System.out.println("No se pudo guardar el catálogo: " + e.getMessage());
            return false;
        }
    }

    // Devuelve null si el archivo no existe o está dañado
    public Catalogo cargar() {
        try {
            ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(ARCHIVO));
            Catalogo catalogo = (Catalogo) entrada.readObject();
            entrada.close();
            return catalogo;
        } catch (Exception e) {
            System.out.println("No se pudo cargar el catálogo: " + e.getMessage());
            return null;
        }
    }

    public boolean existe() {
        return new File(ARCHIVO).exists();
    }
}
