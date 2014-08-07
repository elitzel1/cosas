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

	public Comisiones getComisiones() {
		return comisiones;
	}

	public void setComisiones(Comisiones comisiones) {
		this.comisiones = comisiones;
	}

	public double getEfectivo() {
		return efectivo;
	}

	public void setEfectivo(double efectivo) {
		this.efectivo = efectivo;
	}

	public double getBanamex() {
		return banamex;
	}

	public void setBanamex(double banamex) {
		this.banamex = banamex;
	}

	public double getBanorte() {
		return banorte;
	}

	public void setBanorte(double banorte) {
		this.banorte = banorte;
	}

	public double getSantander() {
		return santander;
	}

	public void setSantander(double santander) {
		this.santander = santander;
	}

	public double getAmex() {
		return amex;
	}

	public void setAmex(double amex) {
		this.amex = amex;
	}

	public double getOther1() {
		return other1;
	}

	public void setOther1(double other1) {
		this.other1 = other1;
	}

	public double getOther2() {
		return other2;
	}

	public void setOther2(double other2) {
		this.other2 = other2;
	}

	public double getOther3() {
		return other3;
	}

	public void setOther3(double other3) {
		this.other3 = other3;
	}

	public double getVendedorComision() {
		return vendedorComision;
	}

	public void setVendedorComision(double vendedorComision) {
		this.vendedorComision = vendedorComision;
	}
	
	
	
}
