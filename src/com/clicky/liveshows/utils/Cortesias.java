package com.clicky.liveshows.utils;

public class Cortesias {

	String tipo;
	int amount;

	public Cortesias(){

	}

	public Cortesias(String tipo, int amout){
		this.tipo=tipo;
		this.amount=amout;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	

}
