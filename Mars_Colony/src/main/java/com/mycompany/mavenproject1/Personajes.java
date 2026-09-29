/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;

/**
 *
 * @author dylnr
 */
public abstract class Personajes extends Thread  {
    private int vidaInicial;
    private int vidaActual;
    private int costo;
    private String nombre;
    private String equipo;
    private int nivel;
    
    public void recibirDaño(double cantidad) {
    vidaActual = (int) (vidaActual - cantidad);
    if (vidaActual <= 0) {
        morir();}
    }

    public void morir() {
        vidaActual = 0;
    }

    public abstract void mejorar(double porcentajeVida, double porcentajeDaño);
}     

