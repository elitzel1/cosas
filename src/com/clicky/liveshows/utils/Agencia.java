package com.clicky.liveshows.utils;

public class Agencia {
	
	String nombre,contacto,mail;
	
	public Agencia(){}
	public Agencia(String nombre,String contacto,String mail){
		this.nombre = nombre;
		this.contacto = contacto;
		this.mail = mail;
	}
	
	public void setNombre(String nombre){
		this.nombre = nombre;
	}
	public String getNombre(){
		return nombre;
	}
	public void setContacto(String contacto){
		this.contacto = contacto;
	}
	public String getContacto(){
		return contacto;
	}
	public void setMail(String mail){
		this.mail = mail;
	}
	public String getMail(){
		return mail;
	}

}
