package com.clicky.liveshows.utils;

import java.util.List;

public class Evento {

	String nombre;
	List<String> artista;
	List<String> fecha;
	String local;
	String capacidad;
	
	public Evento(String nombre){
		this.nombre=nombre;
	}
	
	public Evento(){
		
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public List<String> getArtista() {
		return artista;
	}
	public void setArtista(List<String> artista) {
		this.artista = artista;
	}
	public List<String> getFecha() {
		return fecha;
	}
	public void setFecha(List<String> fecha) {
		this.fecha = fecha;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getCapacidad() {
		return capacidad;
	}
	public void setCapacidad(String capacidad) {
		this.capacidad = capacidad;
	}
	
	public boolean isEmptyArtistas(){
		return artista.isEmpty();
	}
	
	public boolean isEmptyFecha(){
		return fecha.isEmpty();
	}
}
