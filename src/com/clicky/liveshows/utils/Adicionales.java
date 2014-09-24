package com.clicky.liveshows.utils;

public class Adicionales {
	int id;
	String nombre;
	int cantidad;
	int idProd;
	
	
	public Adicionales(int id,String name,int cantidad,int idProd){
		this.id = id;
		this.nombre=name;
		this.cantidad=cantidad;
		this.idProd=idProd;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	public int getIdProd() {
		return idProd;
	}
	public void setIdProd(int idProd) {
		this.idProd = idProd;
	}
}
