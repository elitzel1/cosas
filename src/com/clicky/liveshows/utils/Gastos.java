package com.clicky.liveshows.utils;

public class Gastos {
	
	String concepto;
	double cantidad;
	String comprobante;
	
	public Gastos(){}

	public Gastos(String concepto,double cantidad,String comprobante){
		this.concepto = concepto;
		this.cantidad = cantidad;
		this.comprobante = comprobante;
	}
	
	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public double getCantidad() {
		return cantidad;
	}

	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}

	public String getComprobante() {
		return comprobante;
	}

	public void setComprobante(String comprobante) {
		this.comprobante = comprobante;
	}

}
