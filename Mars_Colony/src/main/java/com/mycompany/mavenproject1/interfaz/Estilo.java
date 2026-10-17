package com.mycompany.mavenproject1.interfaz;

import java.awt.Color;

/**
 * Los COLORES de la interfaz, en un solo lugar.
 *
 * Solo tiene constantes (public static final). Si quieres cambiar un
 * color de todo el juego, lo cambias aquí y listo.
 *
 * Los colores se crean con new Color(rojo, verde, azul), cada uno de 0 a 255.
 *
 * @author dylnr
 */
public class Estilo {

    // ---------- Pantallas ----------
    public static final Color FONDO = new Color(250, 236, 222);   // fondo de las pantallas (crema)
    public static final Color NARANJA = new Color(205, 90, 35);   // títulos

    // ---------- Barra del núcleo ----------
    public static final Color VERDE = new Color(70, 170, 90);
    public static final Color AMARILLO = new Color(230, 180, 40);
    public static final Color ROJO = new Color(210, 60, 50);

    // ---------- Casillas del mapa ----------
    public static final Color ARENA = new Color(236, 205, 165);            // casilla vacía
    public static final Color BORDE_MAPA = new Color(255, 190, 190);       // zona de las criaturas (rosado)
    public static final Color CASILLA_DEFENSA = new Color(205, 225, 255);  // celeste
    public static final Color CASILLA_CRIATURA = new Color(250, 150, 130); // salmón
    public static final Color CASILLA_NUCLEO = new Color(200, 245, 200);   // verde claro
    public static final Color CASILLA_SELECCION = Color.YELLOW;            // la que se está viendo
    public static final Color CASILLA_GOLPE = new Color(255, 60, 40);       // destello al recibir un golpe
}
