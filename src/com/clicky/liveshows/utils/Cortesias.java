package com.clicky.liveshows.utils;

public class Cortesias {

	int id;
	String tipo;
	int amount;

	public Cortesias(){

	}

	public Cortesias(int id,String tipo, int amout){
		this.id = id;
		this.tipo=tipo;
		this.amount=amout;
	}

	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id=id;
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
