package com.clicky.liveshows.utils;

public class Taxes {
	
	int id;
	String name;
	int amount;
	
	public Taxes(String name,int amount){
		this.name=name;
		this.amount=amount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAmount() {
		return amount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	

}
