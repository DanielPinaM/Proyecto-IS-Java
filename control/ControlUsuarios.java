/**
 * 
 */
package control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import exception.IDUsuarioDuplicado;
import exception.IDUsuarioNoExiste;
import fachadas.FachadaUsuarios;
import iFachadas.IFachadaUsuarios;
import modelodeDominio.Empleado;
import modelodeDominio.Encargado;
import modelodeDominio.Producto;
import modelodeDominio.Usuario;
import transfer.TransferUsuarioImp;


/**
 * @author Naik
 *
 */

/**
 * 
 * @author Cille
 *
 */
public class ControlUsuarios {
	protected IFachadaUsuarios IFachadaUsu;
	protected Usuario usuarioActual;
	//borrar
	protected TransferUsuarioImp tUsuarioImp;
	public ControlUsuarios() {
		this.IFachadaUsu = new FachadaUsuarios();
	}

	/**
	 * 
	 * @param usu a registrarse
	 * @throws SQLException excepcion
	 * @throws IDUsuarioDuplicado excepcion
	 */
	public void registrarse(Usuario usu)throws SQLException, IDUsuarioDuplicado{
		this.IFachadaUsu.registrarse(new TransferUsuarioImp(usu.getNombre(), usu.getApellidos(), usu.getNick(), usu.getcontraseña(), usu.getPuntos(), usu.getCorreo(), usu.getTelefono()));
	}
	
	/**
	 * 
	 * @param e encargado a dar de alta
	 * @throws SQLException excepcion
	 * @throws IDUsuarioDuplicado excepcion
	 */
	public void altaEncargado(Encargado e) throws SQLException, IDUsuarioDuplicado {
		this.IFachadaUsu.altaEncargado(e);
	}
	
	/**
	 * 
	 * @param e empleado a dar de alta 
	 * @throws SQLException excepcion
	 * @throws IDUsuarioDuplicado excepcion
	 */
	public void altaEmpleado(Empleado e) throws SQLException, IDUsuarioDuplicado {
		this.IFachadaUsu.altaEmpleado(e);
	}
	
	/**
	 * 
	 * @param usu usuario a darse de baja 
	 * @throws Exception excepcion
	 */
	public void darseBaja(Usuario usu)throws Exception{
		this.IFachadaUsu.darseBaja(new TransferUsuarioImp(usu.getNick(), usu.getTipo()));
	}
	
	/**
	 * 
	 * @param nick para dar de baja
	 * @throws IDUsuarioNoExiste excepcion
	 */
	public void darDeBajaA(String nick) throws IDUsuarioNoExiste {
		this.IFachadaUsu.darDeBajaA(nick);
	}
	
	
	/**
	 * 
	 * @param usu a identificarse
	 * @return Usuario identificado
	 * @throws SQLException excepcion 
	 * @throws IDUsuarioNoExiste excepcion
	 */
	public Usuario identificarse(Usuario usu)throws SQLException, IDUsuarioNoExiste{
		TransferUsuarioImp tU = new TransferUsuarioImp();
		tU.setNick(usu.getNick());
		tU.setcontraseña(usu.getcontraseña());
		return this.IFachadaUsu.identificarse(tU);
	}
	
	/**
	 * 
	 * @throws SQLException excepcion
	 */
	public void cerrarSesion() throws SQLException {
		this.IFachadaUsu.cerrarSesion();
	}
	
	/**
	 * 
	 * @param usu con los datos nuevos 
	 * @throws SQLException excepcion
	 * @throws IDUsuarioNoExiste excepcion
	 */
	public void modificarDatosAcceso(Usuario usu)throws SQLException, IDUsuarioNoExiste{
		//TODO el segundo usuario debe ser la selección del usuarioActual en la GUI.
		this.IFachadaUsu.modificarDatosAcceso(new TransferUsuarioImp(usu.getNombre(), usu.getApellidos(), usu.getNick(), usu.getcontraseña(), usu.getPuntos(), usu.getCorreo(), usu.getTelefono()));
	}
	
	/**
	 * 
	 * @param id a comprobar si existe
	 * @throws SQLException excepcion
	 * @throws IDUsuarioDuplicado excepcion
	 */
	public void existeIdUser(String id) throws SQLException, IDUsuarioDuplicado{
		this.IFachadaUsu.existeId(id);
	}
	
	/**
	 * 
	 * @return usuarios totales 
	 * @throws IOException excepcion
	 * @throws SQLException excepcion
	 */
	public List<Usuario> consultarUsuarios() throws IOException, SQLException {
		return this.IFachadaUsu.consultarUsuarios();
	}


	/**
	 * 
	 * @param u filtro de usuarios
	 * @return usuarios filtrados
	 * @throws SQLException
	 */
	public List<Usuario> filtrarUsu(Usuario u) throws SQLException {
		return this.IFachadaUsu.filtrarUsu(u);
		
	}
}
