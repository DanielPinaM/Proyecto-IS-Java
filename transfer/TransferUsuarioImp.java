/**
 * 
 */
package transfer;


/**
 * @author Naik
 *
 */
@SuppressWarnings("serial")
public class TransferUsuarioImp implements TransferUsuario {

	private String nombre;
	private String apellidos;
	private String contrase�a;
	private String nick;
	private String id;
	private int puntos;
	private String correo;
	private String telefono;
	private String tipo;
	
	/**
	 * @param nick
	 * @param tipo
	 */
	public TransferUsuarioImp(String nick, String tipo) {
		this.nick = nick;
		this.tipo = tipo;
	}
	
	/**
	 * 
	 * @param nick
	 */
	public TransferUsuarioImp(String nick) {
		this.nick = nick;
		this.tipo = "Usuario";
	}
	
	/**
	 * @param nombre
	 * @param apellidos
	 * @param contrase�a
	 * @param nick
	 * @param correo
	 * @param puntos
	 * @param telefono
	 */
	public TransferUsuarioImp(String nombre, String apellidos, String nick, String contrase�a, int puntos, String correo, String telefono) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.contrase�a = contrase�a;
		this.nick = nick;
		this.puntos = puntos;
		this.correo = correo;
		this.telefono = telefono;
		this.tipo = "Usuario";
	}
	
	public TransferUsuarioImp() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @return the contrase�a
	 */
	public String getcontrase�a() {
		return contrase�a;
	}

	/**
	 * @return the nick
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return nick;
	}

	/**
	 * @return the puntos
	 */
	public int getPuntos() {
		return puntos;
	}

	/**
	 * @param nombre the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	

	/**
	 * @param contrase�a the contrase�a to set
	 */
	public void setcontrase�a(String contrase�a) {
		this.contrase�a = contrase�a;
	}

	/**
	 * @param nick the nick to set
	 */
	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public String getTipo() {
		return this.tipo;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param puntos the puntos to set
	 */
	public void setPuntos(int puntos) {
		this.puntos = puntos;
	}

	@Override
	public String getApellidos() {
		return this.apellidos;
	}

	@Override
	public String getCorreo() {
		return this.correo;
	}

	@Override
	public String getTelefono() {
		// TODO Auto-generated method stub
		return this.telefono;
	}

	







}
