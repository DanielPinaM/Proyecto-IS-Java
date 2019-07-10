package modelodeDominio;

import java.io.Serializable;

public interface InterfazProducto extends Serializable{

	/**
	 * 
	 * @return
	 */
	public String getId();
	
	/**
	 * 
	 * @return
	 */
	public String getMarca();
	
	/**
	 * 
	 * @return
	 */
	public String getModelo();
	
	/**
	 * 
	 * @return
	 */
	public double getTalla();
	
	/**
	 * 
	 * @return
	 */
	public int getCantidad();

	/**
	 * 
	 * @return
	 */
	public double getPrecio();
}
