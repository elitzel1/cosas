package com.clicky.liveshows.utils;

public class Talla {

	String talla;
	String cantidad;
	
	public Talla(String talla,String cantidad){
		this.talla=talla;
		this.cantidad=cantidad;
	}
	
	public String getTalla() {
		return talla;
	}
	public void setTalla(String talla) {
		this.talla = talla;
	}
	public String getCantidad() {
		return cantidad;
	}
	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}
}
