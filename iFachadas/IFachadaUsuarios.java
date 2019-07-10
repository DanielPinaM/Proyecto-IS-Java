/**
 * 
 */
package iFachadas;

import java.sql.SQLException;
import java.util.List;

import modelodeDominio.Empleado;
import modelodeDominio.Encargado;
import modelodeDominio.Usuario;
import exception.IDUsuarioDuplicado;
import exception.IDUsuarioNoExiste;
import transfer.TransferUsuario;

/**
 * @author Naik
 *
 */
public interface IFachadaUsuarios {
	/**
	 * 
	 * @param usu a registrarse
	 * @throws SQLException excepcion
	 * @throws IDUsuarioDuplicado excepcion
	 */
	public void registrarse(TransferUsuario usu) throws SQLException, IDUsuarioDuplicado;
	/**
	 * 
	 * @param usu a darse de baja
	 * @throws SQLException excepcion
	 * @throws Exception excepcion
	 */
	public void darseBaja(TransferUsuario usu)throws SQLException, Exception;
	/**
	 * 
	 * @param datos nuevos para usuario
	 * @throws SQLException excepcion
	 * @throws IDUsuarioNoExiste excepcion
	 */
	public void modificarDatosAcceso(TransferUsuario datos)throws SQLException, IDUsuarioNoExiste;
	/**
	 * 
	 * @param usu a identificarse
	 * @return Usuario identificado
	 * @throws SQLException excepcion
	 * @throws IDUsuarioNoExiste excepcion
	 */
	public Usuario identificarse(TransferUsuario usu)throws SQLException, IDUsuarioNoExiste;
	/**
	 * 
	 * @param e empleado a dar de alta
	 * @throws SQLException excepcion
	 * @throws IDUsuarioDuplicado excepcion
	 */
	public void altaEmpleado(Empleado e)throws SQLException, IDUsuarioDuplicado;
	/**
	 * 
	 * @param e encargado a dar de alta
	 * @throws SQLException excepcion
	 * @throws IDUsuarioDuplicado excepcion
	 */
	public void altaEncargado(Encargado e) throws SQLException, IDUsuarioDuplicado;
	
	/**
	 * 
	 * @throws SQLException excepcion
	 */
	public void cerrarSesion() throws SQLException;
	/**
	 * 
	 * @param id si existe
	 * @throws SQLException excepcion
	 * @throws IDUsuarioDuplicado excepcion
	 */
	public void existeId(String id) throws SQLException, IDUsuarioDuplicado;
	/**
	 * 
	 * @param nick a dar de baja
	 * @throws IDUsuarioNoExiste excepcion
	 */
	public void darDeBajaA(String nick) throws IDUsuarioNoExiste;
	/**
	 * 
	 * @return usuarios en la bd
	 * @throws SQLException excepcion
	 */
	public List<Usuario> consultarUsuarios() throws SQLException;
	/**
	 * 
	 * @param u parametro para filtrar
	 * @return usuarios que coinciden 
	 * @throws SQLException excepcion
	 */
	public List<Usuario> filtrarUsu(Usuario u) throws SQLException;
}
