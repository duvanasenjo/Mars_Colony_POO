/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mavenproject1;

import com.mycompany.mavenproject1.interfaz.VentanaJuego;

/**
 * Clase principal del proyecto (la que corre NetBeans con "Run Project").
 * Abre el PROGRAMA 1: la ventana del juego.
 *
 * El PROGRAMA 2 (administración) se abre aparte, con el main de
 * VentanaAdmin (clic derecho en VentanaAdmin.java > Run File).
 *
 * @author duvan
 */
public class Mars_Colony {

    public static void main(String[] args) {
        VentanaJuego ventana = new VentanaJuego();
        ventana.setVisible(true);
    }
}
