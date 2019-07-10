package modelodeDominio;

import java.io.Serializable;
import java.util.Date;

public interface InterfazVenta extends Serializable{
	
	/**
	 * 
	 * @return idProducto
	 */
	public String getIdProducto();
	/**
	 * 
	 * @return idUsuario
	 */
	public String getIdUsuario();
	/**
	 * 
	 * @return precio
	 */
	public double getPrecio();
	/**
	 * 
	 * @return modelo
	 */
	public String getModelo();
	/**
	 * 
	 * @return cantidad
	 */
	public int getCantidad();
	
	/**
	 * 
	 * @return Año
	 */
	public int getAño();
	
	/**
	 * 
	 * @return marca
	 */
	public String getMarca();
	
	/**
	 * 
	 * @return talla
	 */
	public double getTalla();

	/**
	 * 
	 * @param Año
	 */
	public void setAño(int Año);

	/**
	 * 
	 * @return mes
	 */
	public int getMes();

	/**
	 * 
	 * @param mes
	 */
	public void setMes(int mes);
	
	/**
	 * 
	 * @return
	 */
	public int getDia();
}
