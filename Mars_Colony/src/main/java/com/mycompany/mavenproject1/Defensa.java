/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;

import java.util.ArrayList;

/**
 *
 * @author duvan
 */
public class Defensa extends Personajes{
    private int capacidad_total;
    private int capacidad_utilizada;
    private int capacidad_restante;
    private static final String EQUIPO = "Defensa";

    public Defensa(int vidaInicial, int costo, String nombre, int nivel) {
        super(vidaInicial, costo, nombre, nivel);
    }
    
    
    public boolean calcular_defensas(ArrayList<Defensa> arr){
        
        return false;
    }

    @Override
    public void mejorar(double porcentajeVida, double porcentajeDaño) {
    }
    
}
