package com.clicky.liveshows.utils;

public class Comisiones {

	int id;
	String name;
	int cantidad;
	String iva;
	String tipo;

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Comisiones(String name, int cantidad, String iva,String tipo){
		this.name = name;
		this.cantidad = cantidad;
		this.iva=iva;
		this.tipo=tipo;
	}
	
	public Comisiones(){
		
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	public String getIva() {
		return iva;
	}
	public void setIva(String iva) {
		this.iva = iva;
	}


}
