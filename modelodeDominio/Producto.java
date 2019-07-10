/**
 * 
 */
package modelodeDominio;

import java.util.Random;

import control.ControlStock;

public class Producto implements InterfazProducto{
	protected String idProducto;
	private String marca;
	private String modelo;
	private double talla;
	protected double precio;
	protected int cantidad;

	/**
	 * 
	 * @param marca
	 * @param modelo
	 * @param talla
	 * @param precio
	 * @param cantidad
	 */
	public Producto(String marca, String modelo, double talla, double precio, int cantidad){
		Random r = new Random();
		String id = String.valueOf((int) (Math.random() * 999999999) + 1);
		//boolean existe = controllerStock.existeId("5");
		
		//while(existe) {
			//existe = controllerStock.existeId(id);
		//}
		
		this.idProducto = id;
		this.marca = marca;
		this.modelo = modelo;
		this.talla = talla;
		this.precio = precio;
		this.cantidad = cantidad;
	}
	
	/**
	 * 
	 * @param id
	 * @param marca
	 * @param modelo
	 * @param talla
	 * @param precio
	 * @param cantidad
	 */
	public Producto(String id,String marca, String modelo, double talla, double precio, int cantidad){
		
		this.idProducto = id;
		this.marca = marca;
		this.modelo = modelo;
		this.talla = talla;
		this.precio = precio;
		this.cantidad = cantidad;
	}
	
	
	public String getId(){
		return this.idProducto;
	}
	
	public int getCantidad(){
		return this.cantidad;
	}

	public double getPrecio() {
		return this.precio;
	}

	public String getMarca() {
		return marca;
	}

	public String getModelo() {
		return modelo;
	}

	public double getTalla() {
		return this.talla;
	}
	
	public void setCantidad(int c) {
		this.cantidad = c;
	}
	
	/**
	 * 
	 */
	public void setId() {
		this.idProducto = String.valueOf((int) (Math.random() * 999999999) + 1);
	}

}
