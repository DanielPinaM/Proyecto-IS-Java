/**
 * 
 */
package transfer;

import java.io.Serializable;

/**
 * @author Naik
 *
 */
public interface TransferUsuario extends Serializable{	
	/**
	 * 
	 * @return nombre
	 */
	public String getNombre();
	/**
	 * 
	 * @return apellidos
	 */
	public String getApellidos();
	/**
	 * 
	 * @return nick
	 */
	public String getNick();
	/**
	 * 
	 * @return puntos
	 */
	public int getPuntos();
	/**
	 * 
	 * @return contrase�a
	 */
	public String getcontrase�a();
	/**
	 * 
	 * @return correo
	 */
	public String getCorreo();
	/**
	 * 
	 * @return telefono
	 */
	public String getTelefono();
	/**
	 * 
	 * @return tipo
	 */
	public String getTipo();
}
