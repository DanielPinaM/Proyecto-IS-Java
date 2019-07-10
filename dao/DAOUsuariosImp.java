/**
 * 
 */
package dao;


import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import modelodeDominio.Empleado;
import modelodeDominio.Encargado;
import modelodeDominio.Producto;
import modelodeDominio.Usuario;
import connection.Conexion;
import exception.IDUsuarioDuplicado;
import exception.IDUsuarioNoExiste;
import iDAO.IdaoUsuarios;
import transfer.TransferUsuario;
import transfer.TransferUsuarioImp;
@SuppressWarnings("static-access")
/**
 * @author Naik
 *
 */
public class DAOUsuariosImp implements IdaoUsuarios {
	protected static Conexion conexion;
	
/**
 * 
 */
	public DAOUsuariosImp(){
		this.conexion = new Conexion();
		this.conexion.conectar();
	}
	
	/**
	 * 
	 */
	@Override
	public TransferUsuario getDatos(String id) throws SQLException, IDUsuarioNoExiste{
		Statement sa = null;
		try{
			sa = this.conexion.getCx().createStatement();
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		ResultSet rs = sa.executeQuery("SELECT * FROM usuario WHERE idUsuario = " + id);
		if(rs.next()){
			String nombre = rs.getDate("nombre").toString();
			String apellidos = rs.getDate("apellidos").toString();
			String contraseña = rs.getDate("contraseña").toString();
			String nick = rs.getDate("nick").toString();
			int puntos = ((Number) rs.getObject("puntos")).intValue();
			String correo = rs.getDate("correo").toString();
			String telefono = rs.getDate("telefono").toString();
			
			return new TransferUsuarioImp(nombre, apellidos, nick, contraseña, puntos, correo, telefono);
		}
		else 
			throw new IDUsuarioNoExiste("ID de usuario no existe.");
		
	}

	@Override
	public void registrarse(TransferUsuario usu) throws SQLException, IDUsuarioDuplicado {
		Statement sa = null;
		try{
			sa = this.conexion.getCx().createStatement();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		sa.executeUpdate("INSERT INTO usuario(Nombre, Apellidos, Nick, contraseña, Puntos, Correo, Telefono, Tipo) VALUES ("
				+ "'"+usu.getNombre()+"'" + "," + "'"+usu.getApellidos()+"'" + "," + "'"+usu.getNick()+"'" + "," + "'"+usu.getcontraseña()+"'" + "," 
				+ "'"+usu.getPuntos()+"'" + "," + "'"+usu.getCorreo()+"'" + "," + "'"+usu.getTelefono()+"'" + "," + "'"+usu.getTipo()+"'" +")");
		
	}

	@Override
	public void darseBaja(TransferUsuario usu) throws Exception {
		Statement sa = null;
		try{
			sa = this.conexion.getCx().createStatement();
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		/*ResultSet rs = sa.executeQuery("SELECT * from ventas WHERE nick = "+ "'"+usu.getNick()+"'");
		if(rs.next())
			sa.executeUpdate("DELETE FROM ventas WHERE nick = " + "'" +usu.getNick()+"'");*/
		//esta comprobación podría quitarse si alterásemos la tabla VENTAS 
		//para poner el atributo idUsuario con ON DELETE CASCADE;
		if(usu.getTipo().equals("Encargado")) {
			ResultSet rs = sa.executeQuery("SELECT * from usuario WHERE Tipo = " + "'Encargado'");
			int cuenta = 0;
			while(rs.next()) {
				cuenta++;
			}
			if(cuenta == 1){
				throw new Exception("Eres el único encargado, no puedes darte de baja !!");
			}
			else {
				sa.executeUpdate("DELETE FROM usuario WHERE nick = " + "'"+usu.getNick()+"'");
			}
		}
		else {
			sa.executeUpdate("DELETE FROM usuario WHERE nick = " + "'"+usu.getNick()+"'");
		}
	}


	/* (non-Javadoc)
	 * @see idao.IdaoUsuarios#identificarse(java.lang.String)
	 */
	@Override
	public Usuario identificarse(TransferUsuario usu) throws SQLException {
		Statement sa = null;
		try{
			sa = this.conexion.getCx().createStatement();
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		ResultSet rs = sa.executeQuery("SELECT * FROM usuario WHERE Nick = " + "'"+usu.getNick()+"'" + " AND contraseña =" + "'"+usu.getcontraseña()+"'");
		if(rs.next()){
			String nombre = (String) rs.getObject("Nombre");
			String apellidos = (String) rs.getObject("Apellidos");
			String contraseña = (String) rs.getObject("contraseña");
			String nick = (String) rs.getObject("Nick");
			int puntos = ((Number) rs.getObject("Puntos")).intValue();
			String correo = (String) rs.getObject("Correo");
			String telefono = (String) rs.getObject("Telefono");
			String tipo = (String) rs.getObject("tipo");
			Usuario usuario = new Usuario(nombre, apellidos, nick, contraseña, puntos, correo, telefono, tipo);
			return usuario;
		}
		return null;
		
	}

	/* (non-Javadoc)
	 * @see idao.IdaoUsuarios#darAltaEmpleado(transfer.TransferUsuario)
	 */
	@Override
	public void altaEmpleado(Empleado usu) throws SQLException, IDUsuarioDuplicado {
		Statement sa = null;
		try{
			sa = this.conexion.getCx().createStatement();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//ResultSet rs = sa.executeQuery("SELECT * FROM usuario where idUsuario = " + "'"+ usu.getNick()+"'");
		//if(rs.next())
			//throw new IDUsuarioDuplicado("ID de usuario ya existe en la base de datos.");
			sa.executeUpdate("INSERT INTO usuario(Nombre, Apellidos, Nick, contraseña, Puntos, Correo, Telefono, Tipo) VALUES ("
					+ "'"+usu.getNombre()+"'" + "," + "'"+usu.getApellidos()+"'" + "," + "'"+usu.getDNI()+"'" + "," + "'"+usu.getcontraseña()+"'" + "," 
					+ "'"+usu.getPuntos()+"'" + "," + "'"+usu.getCorreo()+"'" + "," + "'"+usu.getTelefono()+"'" + "," + "'"+usu.getTipo()+"'" +")");
	}

	@Override
	public void altaEncargado(Encargado usu) throws SQLException, IDUsuarioDuplicado {
		// TODO Auto-generated method stub
		Statement sa = null;
		try{
			sa = this.conexion.getCx().createStatement();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//ResultSet rs = sa.executeQuery("SELECT * FROM usuario where idUsuario = " + "'"+ usu.getNick()+"'");
		//if(rs.next())
			//throw new IDUsuarioDuplicado("ID de usuario ya existe en la base de datos.");
			sa.executeUpdate("INSERT INTO usuario(Nombre, Apellidos, Nick, contraseña, Puntos, Correo, Telefono, Tipo) VALUES ("
					+ "'"+usu.getNombre()+"'" + "," + "'"+usu.getApellidos()+"'" + "," + "'"+usu.getDNI()+"'" + "," + "'"+usu.getcontraseña()+"'" + "," 
					+ "'"+usu.getPuntos()+"'" + "," + "'"+usu.getCorreo()+"'" + "," + "'"+usu.getTelefono()+"'" + "," + "'"+usu.getTipo()+"'" +")");
	}


	/* (non-Javadoc)
	 * @see idao.IdaoUsuarios#existeIDUsuario(java.lang.String)
	 */
	@Override
	public boolean existeIDUsuario(String id) throws SQLException {
		Statement sa = null;
		try{
			sa = this.conexion.getCx().createStatement();
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		ResultSet rs = sa.executeQuery("SELECT nick FROM usuario WHERE nick = " + "'"+id+"'");
		return rs.next();
	}

	/* (non-Javadoc)
	 * @see idao.IdaoUsuarios#modificarDatosAcceso(transfer.TransferUsuario, transfer.TransferUsuario)
	 */
	@Override
	public void modificarDatosAcceso(TransferUsuario datos) throws SQLException, IDUsuarioNoExiste {
		Statement sa = null;
		try{
			sa = this.conexion.getCx().createStatement();
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		
		if(!datos.getNombre().equals(""))
			sa.executeUpdate("UPDATE usuario SET Nombre = " + "'"+datos.getNombre()+"'"+" WHERE nick = " + "'" + datos.getNick()+"'");
		
		if(!datos.getApellidos().equals(""))
			sa.executeUpdate("UPDATE usuario SET Apellidos = " + "'"+datos.getApellidos()+"'"+" WHERE nick = " + "'" + datos.getNick()+"'");
		
		if(!datos.getcontraseña().equals(""))
			sa.executeUpdate("UPDATE usuario SET contraseña = " + "'"+datos.getcontraseña()+"'"+" WHERE nick = " + "'" + datos.getNick()+"'");
		
		if(!datos.getCorreo().equals(""))
			sa.executeUpdate("UPDATE usuario SET Correo = " + "'"+datos.getCorreo()+"'"+" WHERE nick = " + "'" + datos.getNick()+"'");
		
		if(!datos.getTelefono().equals(""))	
			sa.executeUpdate("UPDATE usuario SET Telefono = " +datos.getTelefono()+" WHERE nick = " + "'" + datos.getNick()+"'");	

	}

	@Override
	public void cerrarSesion() throws SQLException {
		Statement sa = null;
		try{
			sa = this.conexion.getCx().createStatement();
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		//if(this.existeIDUsuario(usu.getNick())){
		sa.executeUpdate("DELETE FROM carrito");
		//}
	}
	
	@Override
	public void darDeBajaA(String nick) throws SQLException {
		Statement sa = null;
		try{
			sa = this.conexion.getCx().createStatement();
		}
		catch (SQLException e){
			e.printStackTrace();
		}
	
		sa.executeUpdate("DELETE FROM usuario WHERE nick = " + "'"+nick+"'");
		
	}
	
	@Override
	public List<Usuario> consultarUsuarios() throws SQLException {
		List<Usuario> usuarios = new ArrayList<Usuario>();
		
		Statement sa = null;
		try {
			sa = this.conexion.getCx().createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		ResultSet rs = sa.executeQuery("SELECT * FROM usuario WHERE tipo = " + "'Usuario'"); 
		while (rs.next())
		{	
		   String nombre = (String) rs.getObject("nombre");
		   String apellidos = (String) rs.getObject("apellidos");
		   String nick = (String) rs.getObject("nick");
		   String contraseña =  (String) rs.getObject("contraseña");
		   String correo = (String) rs.getObject("correo");
		   String telefono =  (String) rs.getObject("telefono");
		   String tipo = (String) rs.getObject("tipo");
		   
		   Usuario p = new Usuario(nombre, apellidos, nick, contraseña, correo, telefono, tipo);
		   usuarios.add(p);
		}
		rs.close();
		sa = this.conexion.getCx().createStatement();
		ResultSet rt = sa.executeQuery("SELECT * FROM usuario WHERE tipo = " + "'Empleado'"); 
		while (rt.next())
		{	
			String nombre = (String) rt.getObject("nombre");
		    String apellidos = (String) rt.getObject("apellidos");
		    String nick = (String) rt.getObject("nick");
			String contraseña =  (String) rt.getObject("contraseña");
			String correo = (String) rt.getObject("correo");
			String telefono =  (String) rt.getObject("telefono");
			String tipo = (String) rt.getObject("tipo");
		   
		   
		   Usuario p = new Usuario(nombre, apellidos, nick, contraseña, correo, telefono, tipo);
		   usuarios.add(p);
		}
		rt.close();
		sa = this.conexion.getCx().createStatement();
		ResultSet rr = sa.executeQuery("SELECT * FROM usuario WHERE tipo = " + "'Encargado'"); 
		while (rr.next())
		{	
			String nombre = (String) rr.getObject("nombre");
			String apellidos = (String) rr.getObject("apellidos");
			String nick = (String) rr.getObject("nick");
			String contraseña =  (String) rr.getObject("contraseña");
			String correo = (String) rr.getObject("correo");
			String telefono =  (String) rr.getObject("telefono");
			String tipo = (String) rr.getObject("tipo");
		   
		   
		   Usuario p = new Usuario(nombre, apellidos, nick, contraseña, correo, telefono, tipo);
		   usuarios.add(p);
		}
		rr.close();
		
		//Devolver la lista de usuarios
		return usuarios;
	}
	
	
	@Override
	public List<Usuario> filtrarUsu(Usuario u) throws SQLException {
		List<Usuario> usuarios = new ArrayList<Usuario>();
		Statement sa = null;
		try{
			sa = this.conexion.getCx().createStatement();
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		
		
		if(!u.getNombre().equals("")) {
			if(!u.getApellidos().equals("")) {
				if(!u.getCorreo().equals("")) {
					ResultSet rs = sa.executeQuery("SELECT * FROM usuario WHERE nombre = " + "'"+u.getNombre()+"'" + " AND correo = " + "'"+u.getCorreo()+"'");
					while(rs.next()) {
						String nombre = (String) rs.getObject("nombre");
						String apellidos = (String) rs.getObject("apellidos");
						String nick = (String) rs.getObject("nick");
						String contraseña = (String) rs.getObject("contraseña");
						String correo = (String) rs.getObject("correo");
						String telefono = (String) rs.getObject("telefono");
						String tipo = (String) rs.getObject("tipo");
						
						Usuario p = new Usuario(nombre, apellidos, nick, contraseña, correo, telefono, tipo);
						usuarios.add(p);
					}
				}else {
					ResultSet rs = sa.executeQuery("SELECT * FROM usuario WHERE nombre = " + "'"+u.getNombre()+"'" + " AND apellidos =" + "'"+u.getApellidos()+"'");
					while(rs.next()) {
						String nombre = (String) rs.getObject("nombre");
						String apellidos = (String) rs.getObject("apellidos");
						String nick = (String) rs.getObject("nick");
						String contraseña = (String) rs.getObject("contraseña");
						String correo = (String) rs.getObject("correo");
						String telefono = (String) rs.getObject("telefono");
						String tipo = (String) rs.getObject("tipo");
						
						Usuario p = new Usuario(nombre, apellidos, nick, contraseña, correo, telefono, tipo);
						usuarios.add(p);
					}
				
				}
			}
			else {

				if(!u.getCorreo().equals("")) {
					ResultSet rs = sa.executeQuery("SELECT * FROM usuario WHERE nombre = " + "'"+u.getNombre()+"'" + " AND correo = " + "'"+u.getCorreo()+"'");
					while(rs.next()) {
						String nombre = (String) rs.getObject("nombre");
						String apellidos = (String) rs.getObject("apellidos");
						String nick = (String) rs.getObject("nick");
						String contraseña = (String) rs.getObject("contraseña");
						String correo = (String) rs.getObject("correo");
						String telefono = (String) rs.getObject("telefono");
						String tipo = (String) rs.getObject("tipo");
						
						Usuario p = new Usuario(nombre, apellidos, nick, contraseña, correo, telefono, tipo);
						usuarios.add(p);
					}
				}else {
					ResultSet rs = sa.executeQuery("SELECT * FROM usuario WHERE nombre = " + "'"+u.getNombre()+"'");
					while(rs.next()) {
						String nombre = (String) rs.getObject("nombre");
						String apellidos = (String) rs.getObject("apellidos");
						String nick = (String) rs.getObject("nick");
						String contraseña = (String) rs.getObject("contraseña");
						String correo = (String) rs.getObject("correo");
						String telefono = (String) rs.getObject("telefono");
						String tipo = (String) rs.getObject("tipo");
						
						Usuario p = new Usuario(nombre, apellidos, nick, contraseña, correo, telefono, tipo);
						usuarios.add(p);
					}
				
				}
			
			}
		}else {

			if(!u.getApellidos().equals("")) {
				if(!u.getCorreo().equals("")) {
					ResultSet rs = sa.executeQuery("SELECT * FROM usuario WHERE apellidos =" + "'"+u.getApellidos()+"'" + " AND correo = " + "'"+u.getCorreo()+"'");
					while(rs.next()) {
						String nombre = (String) rs.getObject("nombre");
						String apellidos = (String) rs.getObject("apellidos");
						String nick = (String) rs.getObject("nick");
						String contraseña = (String) rs.getObject("contraseña");
						String correo = (String) rs.getObject("correo");
						String telefono = (String) rs.getObject("telefono");
						String tipo = (String) rs.getObject("tipo");
						
						Usuario p = new Usuario(nombre, apellidos, nick, contraseña, correo, telefono, tipo);
						usuarios.add(p);
					}
				}else {
					ResultSet rs = sa.executeQuery("SELECT * FROM usuario WHERE apellidos =" + "'"+u.getApellidos()+"'");
					while(rs.next()) {
						String nombre = (String) rs.getObject("nombre");
						String apellidos = (String) rs.getObject("apellidos");
						String nick = (String) rs.getObject("nick");
						String contraseña = (String) rs.getObject("contraseña");
						String correo = (String) rs.getObject("correo");
						String telefono = (String) rs.getObject("telefono");
						String tipo = (String) rs.getObject("tipo");
						
						Usuario p = new Usuario(nombre, apellidos, nick, contraseña, correo, telefono, tipo);
						usuarios.add(p);
					}
				
				}
			}
			else {

				if(!u.getCorreo().equals("")) {
					ResultSet rs = sa.executeQuery("SELECT * FROM usuario WHERE correo = " + "'"+u.getCorreo()+"'");
					while(rs.next()) {
						String nombre = (String) rs.getObject("nombre");
						String apellidos = (String) rs.getObject("apellidos");
						String nick = (String) rs.getObject("nick");
						String contraseña = (String) rs.getObject("contraseña");
						String correo = (String) rs.getObject("correo");
						String telefono = (String) rs.getObject("telefono");
						String tipo = (String) rs.getObject("tipo");
						
						Usuario p = new Usuario(nombre, apellidos, nick, contraseña, correo, telefono, tipo);
						usuarios.add(p);
					}
				}else {
					ResultSet rs = sa.executeQuery("SELECT * FROM usuario");
					while(rs.next()) {
						String nombre = (String) rs.getObject("nombre");
						String apellidos = (String) rs.getObject("apellidos");
						String nick = (String) rs.getObject("nick");
						String contraseña = (String) rs.getObject("contraseña");
						String correo = (String) rs.getObject("correo");
						String telefono = (String) rs.getObject("telefono");
						String tipo = (String) rs.getObject("tipo");
						
						Usuario p = new Usuario(nombre, apellidos, nick, contraseña, correo, telefono, tipo);
						usuarios.add(p);
					}
				
				}
			
			}
		
		}
		
		
		return usuarios;
	}	

}




