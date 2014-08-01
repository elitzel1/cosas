package com.clicky.liveshows.utils;

import java.util.List;

public class Stand {

	long id;
	String name;
	String encargado;
	Comisiones comisiones;
	List<Product> products;
	double efectivo,banamex,banorte,santander,amex,other1,other2,other3;
	double vendedorComision;
	boolean abierto;
	
	public Stand(){}
	
	public Stand(long id, String name,String encargado,Comisiones comisiones){
		this.id  = id;
		this.name=name;
		this.encargado=encargado;
		this.comisiones=comisiones;
	}
	
	public Stand(long id, String name,String encargado,Comisiones comisiones, double efectivo, double banamex, double banorte, 
			double santander, double amex, double other1, double other2, double other3,double vendedorComision, boolean abierto){
		this.id  = id;
		this.name=name;
		this.encargado=encargado;
		this.comisiones=comisiones;
		this.efectivo = efectivo;
		this.banamex = banamex;
		this.banorte = banorte;
		this.santander = santander;
		this.amex = amex;
		this.other1 = other1;
		this.other2 = other2;
		this.other3 = other3;
		this.vendedorComision = vendedorComision;
		this.abierto = abierto;
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
	
	public void setIngresos(double efectivo, double banamex, double banorte, double santander, double amex, double other1, double other2, double other3, double vendedorComision){
		this.efectivo = efectivo;
		this.banamex = banamex;
		this.banorte = banorte;
		this.santander = santander;
		this.amex = amex;
		this.other1 = other1;
		this.other2 = other2;
		this.other3 = other3;
		this.vendedorComision = vendedorComision;
		this.abierto = false;
	}
	
	public boolean isOpened(){
		return abierto;
	}
	
	public void setAbierto(boolean abierto){
		this.abierto = abierto;
	}
	
}
