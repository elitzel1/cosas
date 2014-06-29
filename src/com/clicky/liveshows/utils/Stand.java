package com.clicky.liveshows.utils;

import java.util.List;

public class Stand {

	long id;
	String name;
	String encargado;
	Comisiones comisiones;
	List<Product> products;
	
	public Stand(){
		
	}
	
	public Stand(long id, String name,String encargado,Comisiones comisiones){
		this.id  = id;
		this.name=name;
		this.encargado=encargado;
		this.comisiones=comisiones;
	}
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEncargado() {
		return encargado;
	}
	public void setEncargado(String encargado) {
		this.encargado = encargado;
	}
	public Comisiones getComision() {
		return comisiones;
	}
	public void setComision(Comisiones comision) {
		this.comisiones = comision;
	}
//	public String getTipoComision() {
//		return tipoComision;
//	}
//	public void setTipoComision(String neto) {
//		this.tipoComision = neto;
//	}
	
}
