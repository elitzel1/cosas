package com.clicky.liveshows.utils;

import java.util.List;

public class Stand {

	long id;
	String name;
	String encargado;
	Comisiones comisiones;
	List<Product> products;
	double efectivo,banamex,banorte,santander,amex,other;
	
	public Stand(){}
	
	public Stand(long id, String name,String encargado,Comisiones comisiones){
		this.id  = id;
		this.name=name;
		this.encargado=encargado;
		this.comisiones=comisiones;
	}
	
	public Stand(long id, String name,String encargado,Comisiones comisiones, double efectivo, double banamex, double banorte, double santaner, double amex, double other){
		this.id  = id;
		this.name=name;
		this.encargado=encargado;
		this.comisiones=comisiones;
		this.efectivo = efectivo;
		this.banamex = banamex;
		this.banorte = banorte;
		this.santander = santaner;
		this.amex = amex;
		this.other = other;
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
	
	public boolean isOpened(){
		if(this.efectivo == 0 && this.banamex == 0 && this.banorte == 0 && this.santander == 0 && this.amex == 0 && this.other == 0)
			return true;
		else
			return false;
	}
	
}
