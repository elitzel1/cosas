package com.clicky.liveshows.utils;

public class Local {
	String nombre;
	int capacidad;
	
	public Local(){} 
	public Local(String nombre){
		this.nombre=nombre;
	}
	public Local(String nombre, int capacidad){
		this.nombre=nombre;
		this.capacidad= capacidad;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getCapacidad() {
		return capacidad;
	}
	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}


}
