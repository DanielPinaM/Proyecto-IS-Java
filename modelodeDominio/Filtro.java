package modelodeDominio;

public class Filtro {
	
	protected String adidas;
	protected String nike;
	protected String puma;
	protected String newBalance;
	protected String modelo;
	protected String talla;
	protected String precio;
	
	/**
	 * 
	 * @param a String Adidas
	 * @param n String Nike
	 * @param p String Puma
	 * @param nb String New Balance
	 * @param model String modelo
	 * @param talla String talla 
	 * @param precio String precio
	 */
	public Filtro(String a, String n, String p, String nb, String model, String talla, String precio) {
		this.adidas = a;
		this.nike = n;
		this.puma = p;
		this.newBalance = nb;
		this.modelo = model;
		this.talla = talla;
		this.precio = precio;
	}
	
	/**
	 * 
	 * @return adidas nombre
	 */
	public String getAdidas() {
		return this.adidas;
	}
	/**
	 * 
	 * @return nike nombre
	 */
	public String getNike() {
		return this.nike;
	}
	/**
	 * 
	 * @return puma nombre
	 */
	public String getPuma() {
		return this.puma;
	}
	/**
	 * 
	 * @return newBalance nombre
	 */ 
	public String getNewBalance() {
		return this.newBalance;
	}
	/**
	 * 
	 * @return modelo
	 */
	public String getModelo() {
		return this.modelo;
	}
	/**
	 * 
	 * @return talla
	 */
	public String getTalla() {
		return this.talla;
	}
	/**
	 * 
	 * @return precio
	 */
	public String getPrecio() {
		return this.precio;
	}
	
}
