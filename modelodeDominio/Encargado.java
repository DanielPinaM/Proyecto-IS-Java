/**
 * 
 */
package modelodeDominio;

import java.util.ArrayList;
import java.util.List;


public class Encargado {
	
	protected String nombre;
	protected String apellidos;
	protected int puntos;
	protected String DNI;
	protected String contraseña;
	protected String correo;
	protected String telefono;
	protected String tipo;
	
	/**
	 * 
	 * @param nombre
	 * @param apellidos
	 * @param dni
	 * @param contraseña
	 * @param correo
	 * @param telefono
	 */
	public Encargado(String nombre, String apellidos, String dni, String contraseña, String correo, String telefono){
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.DNI = dni;
		this.contraseña = contraseña;
		this.correo = correo;
		this.telefono = telefono;
		this.tipo = "Encargado";
	}
	
	/**
	 * 
	 * @return nombre
	 */
	public String getNombre() {
		return nombre;
	}
	/**
	 * 
	 * @return apellidos
	 */
	public String getApellidos() {
		return apellidos;
	}
	/**
	 *  
	 * @return puntos
	 */
	public int getPuntos() {
		return puntos;
	}
	/**
	 * 
	 * @return tipo
	 */
	public String getTipo() {
		return tipo;
	}
	/**
	 * 
	 * @return DNI
	 */
	public String getDNI() {
		return DNI;
	}
	/**
	 * 
	 * @return contraseña
	 */
	public String getcontraseña() {
		return contraseña;
	}
	/**
	 * 
	 * @return correo
	 */
	public String getCorreo() {
		return this.correo;
	}
	/**
	 * 
	 * @return telefono
	 */ 
	public String getTelefono() {
		return this.telefono;
	}
	/**
	 * 
	 * @param nombre
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	/**
	 * 
	 * @param apellidos
	 */
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	/**
	 * 
	 * @param puntos
	 */
	public void setPuntos(int puntos) {
		this.puntos = puntos;
	}
	/**
	 * 
	 * @param dni
	 */
	public void setDNI(String dni) {
		this.DNI = dni;
	}
	/**
	 * 
	 * @param contraseña
	 */
	public void setcontraseña(String contraseña) {
		this.contraseña = contraseña;
	}
	/**
	 * 
	 * @param correo
	 */
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	/**
	 * 
	 * @param telefono
	 */
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	
}