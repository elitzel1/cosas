package com.clicky.liveshows.utils;

import java.util.ArrayList;
import java.util.List;

public class Product {
	
	int id;
	int idStand;
	String nombre;
	String tipo;
	String artista;
	String precio;
	String talla;
	int cantidad;
	int totalCantidad=0;
	int cantidadStand;
	List<Comisiones> comisiones;
	List<Adicionales> adicional;
	List<Taxes> taxes;
	int id_imagen;
	String path_imagen;
	int prodNo; //Productos no vendidos
	List<Cortesias> cortesias;
	
	public List<Cortesias> getCortesias() {
		return cortesias;
	}

	public void setCortesias(List<Cortesias> cortesias) {
		this.cortesias = cortesias;
	}
	
	public int sizeCortesias(){
		return cortesias.size();
	}
	
	public void addCortesia(Cortesias cortesia){
		cortesias.add(cortesia);
	}

	public int getProdNo() {
		return prodNo;
	}

	public void setProdNo(int prodNo) {
		this.prodNo = prodNo;
	}

	public Product(){
		adicional = new ArrayList<Adicionales>();
		comisiones = new ArrayList<Comisiones>();
		taxes = new ArrayList<Taxes>();
		cortesias = new ArrayList<Cortesias>();
	}
	
	public Product(String nombre, String tipo, String artista, String precio, String talla, int cantidad, List<Comisiones> comisiones, String path_img)
	{
		adicional = new ArrayList<Adicionales>();
		this.comisiones = new ArrayList<Comisiones>();
		taxes=new ArrayList<Taxes>();
		cortesias = new ArrayList<Cortesias>();
		this.nombre=nombre;
		this.tipo=tipo;
		this.artista=artista;
		this.precio=precio;
		this.talla=talla;
		this.cantidad=cantidad;
		this.comisiones=comisiones;
		this.path_imagen=path_img;
	}
	
	public void setIdStand(int idStand){
		this.idStand = idStand;
	}
	public int getIdStand(){
		return this.idStand;
	}
	public List<Taxes> getTaxes() {
		return taxes;
	}

	public void setTaxes(List<Taxes> taxes) {
		this.taxes = taxes;
	}

	public List<Comisiones> getComisiones() {
		return comisiones;
	}

	public void setComisiones(List<Comisiones> comisiones) {
		this.comisiones = comisiones;
	}

	public int getCantidadStand() {
		return cantidadStand;
	}

	public void setCantidadStand(int cantidadStand) {
		this.cantidadStand = cantidadStand;
	}

	public int getTotalCantidad() {
		return totalCantidad;
	}

	public void setTotalCantidad(int totalCantidad) {
		this.totalCantidad = totalCantidad;
	}
	
	public List<Adicionales> getAdicional() {
		return adicional;
	}

	public void setAdicional(List<Adicionales> adicional) {
		this.adicional = adicional;
	}

	public void setAdicional(String name,int cantidad,int id){
		this.adicional.add(new Adicionales(name,cantidad,id));
	}
	
	public Adicionales getAdicionalA(int position){
		return this.adicional.get(position);
	}
	
	public int getAdicionalSize(){
		return this.adicional.size();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getPath_imagen() {
		return path_imagen;
	}
	public void setPath_imagen(String path_imagen) {
		this.path_imagen = path_imagen;
	}
	public int getId_imagen() {
		return id_imagen;
	}
	public void setId_imagen(int id_imagen) {
		this.id_imagen = id_imagen;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getArtista() {
		return artista;
	}
	public void setArtista(String artista) {
		this.artista = artista;
	}
	public String getPrecio() {
		return precio;
	}
	public void setPrecio(String precio) {
		this.precio = precio;
	}
	public String getTalla() {
		return talla;
	}
	public void setTalla(String talla) {
		this.talla = talla;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

}
