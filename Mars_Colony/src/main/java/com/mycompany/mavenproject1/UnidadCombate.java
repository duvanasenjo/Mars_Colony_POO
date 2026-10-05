package com.mycompany.mavenproject1;

/**
 *
 * @author dylnr
 */
public abstract class UnidadCombate {

    private String nombre;
    private int vidaMaxima;
    private int vidaActual;
    private int daño;
    private int nivel;
    private Posicion posicion; // null hasta que el Mapa la coloque

    public UnidadCombate(String nombre, int vidaMaxima, int daño, int nivel) {
        this.nombre = nombre;
        this.vidaMaxima = vidaMaxima;
        this.vidaActual = vidaMaxima;
        this.daño = daño;
        this.nivel = nivel;
    }

    // Resta vida; nunca baja de 0
    public void recibirDaño(int cantidad) {
        vidaActual = Math.max(0, vidaActual - cantidad);
    }

    public boolean estaViva() {
        return vidaActual > 0;
    }

    // Porcentajes de 0.05 a 0.20 (5% a 20%). Igual para todas las unidades.
    public void mejorar(double porcentajeVida, double porcentajeDaño) {
        vidaMaxima = (int) Math.round(vidaMaxima * (1 + porcentajeVida));
        daño = (int) Math.round(daño * (1 + porcentajeDaño));
        vidaActual = vidaMaxima;
        nivel++;
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

    public Posicion getPosicion() {
        return posicion;
    }

    // La usa el Mapa al colocar o mover la unidad
    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }
}
