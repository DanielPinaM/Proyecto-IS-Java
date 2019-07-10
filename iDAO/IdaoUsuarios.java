/**
 * 
 */
package iDAO;



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
public interface IdaoUsuarios {
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
	public void darseBaja(TransferUsuario usu) throws SQLException, Exception;
	/**
	 * 
	 * @param datos con los nuevos campos del usuarui
	 * @throws SQLException excepcion
	 * @throws IDUsuarioNoExiste excepcion
	 */
	public void modificarDatosAcceso(TransferUsuario datos) throws SQLException, IDUsuarioNoExiste;
	/**
	 * 
	 * @param e empleado a dar de alta
	 * @throws SQLException excepcion
	 * @throws IDUsuarioDuplicado excepcion
	 */
	public void altaEmpleado(Empleado e) throws SQLException, IDUsuarioDuplicado;
	
	
	/**
	 * 
	 * @param e encargado a dar de alta
	 * @throws SQLException excepcion
	 * @throws IDUsuarioDuplicado excepcion
	 */
	public void altaEncargado(Encargado e) throws SQLException, IDUsuarioDuplicado;
	
	
	/**
	 * 
	 * @param id para comprobar que existe ese usuario
	 * @return boolean true si existe
	 * @throws SQLException excepcion
	 */
	public boolean existeIDUsuario(String id) throws SQLException;
	
	/**
	 * @param usu a identificarse
	 * @return Usuario identificado
	 * @throws SQLException excepcion 
	 * @throws IDUsuarioNoExiste excepcion
	 */
	Usuario identificarse(TransferUsuario usu) throws SQLException, IDUsuarioNoExiste;
	
	/**
	 * @param id del usuario a obtener sus datos
	 * @return TransferUsuario usuario transfer con los datos
	 * @throws IDUsuarioNoExiste excepcion
	 * @throws SQLException excepcion
	 */
	TransferUsuario getDatos(String id) throws SQLException, IDUsuarioNoExiste;
	
	/**
	 * 
	 * 
	 * @throws SQLException excepcion
	 */
	public void cerrarSesion() throws SQLException;
	/**
	 * 
	 * @param nick a dar de baja
	 * @throws SQLException excepcion
	 */
	public void darDeBajaA(String nick) throws SQLException;
	/**
	 * 
	 * @return usuarios totales
	 * @throws SQLException excepcion
	 */
	public List<Usuario> consultarUsuarios() throws SQLException;
	/**
	 * 
	 * @param u filtro para obtener usuarios deseados
	 * @return usuarios que cumplen filtro
	 * @throws SQLException
	 */
	public List<Usuario> filtrarUsu(Usuario u) throws SQLException;
	
}
