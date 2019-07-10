package modelodeDominio;

import java.util.Random;

public class Usuario {
	
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
	 * @return nick
	 */
	public String getNick() {
		return nick;
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
	 * @param nick
	 */
	public void setNick(String nick) {
		this.nick = nick;
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
	
	/*public Usuario(String id, String nombre) {
		this.id = id;
		this.nombre = nombre;
	}*/
	
	/**
	 * 
	 * @param nick
	 * @param contraseña
	 */
	public Usuario(String nick, String contraseña) {
		this.nick = nick;
		this.contraseña = contraseña;
	}
	
	/**
	 * 
	 */
	public Usuario(){
		
	}

	/**
	 * 
	 * @param nombre
	 * @param apellidos
	 * @param nick
	 * @param contraseña
	 * @param puntos
	 * @param correo
	 * @param telefono
	 * @param tipo
	 */
	public Usuario(String nombre, String apellidos, String nick, String contraseña, int puntos, String correo, String telefono, String tipo) {
		super();
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.puntos = puntos;
		this.nick = nick;
		this.contraseña = contraseña;
		this.correo = correo;
		this.telefono = telefono;
		this.tipo = tipo;
	}
	
	/**
	 * 
	 * @param nombre
	 * @param apellidos
	 * @param nick
	 * @param contraseña
	 * @param puntos
	 * @param correo
	 * @param telefono
	 */
	public Usuario(String nombre, String apellidos, String nick, String contraseña, int puntos, String correo, String telefono) {
		super();
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.puntos = puntos;
		this.nick = nick;
		this.contraseña = contraseña;
		this.correo = correo;
		this.telefono = telefono;
		this.tipo = "Usuario";
	}

	/**
	 * 
	 * @param nombre
	 * @param apellidos
	 * @param nick
	 * @param contraseña
	 * @param correo
	 * @param telefono
	 */
	public Usuario(String nombre, String apellidos, String nick, String contraseña, String correo, String telefono) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.puntos = 0;
		this.nick = nick;
		this.contraseña = contraseña;
		this.correo = correo;
		this.telefono = telefono;
		this.tipo = "Usuario";
		//Random r = new Random(System.currentTimeMillis());
		//this.id = Integer.toString(r.nextInt(999999999));
	}
	
	/**
	 * 
	 * @param nombre
	 * @param apellidos
	 * @param contraseña
	 * @param correo
	 * @param telefono
	 */
	public Usuario(String nombre, String apellidos, String contraseña, String correo, String telefono) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.puntos = 0;
		this.nick = nick;
		this.contraseña = contraseña;
		this.correo = correo;
		this.telefono = telefono;
		this.tipo = "Usuario";
		//Random r = new Random(System.currentTimeMillis());
		//this.id = Integer.toString(r.nextInt(999999999));
	}
	
	/**
	 * 
	 * @param nombre
	 * @param apellidos
	 * @param nick
	 * @param contraseña
	 * @param correo
	 * @param telefono
	 * @param tipo
	 */
	public Usuario(String nombre, String apellidos, String nick, String contraseña, String correo, String telefono, String tipo) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.nick = nick;
		this.correo = correo;
		this.telefono = telefono;
		this.tipo = tipo;
	}
	
	/**
	 * 
	 * @param nombre
	 * @param apellido
	 * @param correo
	 */
	public Usuario(String nombre, String apellido, String correo) {
		this.nombre = nombre;
		this.apellidos = apellido;
		this.correo = correo;
	}
	

	protected String nombre;
	protected String apellidos;
	protected int puntos;
	protected String nick;
	protected String contraseña;
	protected String correo;
	protected String telefono;
	protected String tipo;



}