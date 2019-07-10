/**
 * 
 */
package fachadas;

import java.sql.SQLException;
import java.util.List;

import modelodeDominio.Empleado;
import modelodeDominio.Encargado;
import modelodeDominio.Usuario;
import exception.IDUsuarioDuplicado;
import exception.IDUsuarioNoExiste;
import iFachadas.IFachadaUsuarios;
import iSASI.IsasiUsuarios;
import sasi.SASIUsuariosImp;
import transfer.TransferUsuario;



/**
 * @author Naik
 *
 */
public class FachadaUsuarios implements IFachadaUsuarios {

	protected IsasiUsuarios isasiUsuarios;
	
	
	public FachadaUsuarios(){
		this.isasiUsuarios = new SASIUsuariosImp();
	}
	/* (non-Javadoc)
	 * @see ifachada.IFachadaUsuarios#darseAlta(transfer.TransferUsuario)
	 */
	@Override
	public void registrarse(TransferUsuario usu)throws SQLException, IDUsuarioDuplicado {
		this.isasiUsuarios.registrarse(usu);
	}

	/* (non-Javadoc)
	 * @see ifachada.IFachadaUsuarios#darseBaja(transfer.TransferUsuario)
	 */
	@Override
	public void darseBaja(TransferUsuario usu) throws Exception{
		this.isasiUsuarios.darseBaja(usu);
	}

	/* (non-Javadoc)
	 * @see ifachada.IFachadaUsuarios#modificarDatosAcceso(transfer.TransferUsuario, transfer.TransferUsuario)
	 */
	@Override
	public void modificarDatosAcceso(TransferUsuario datos)throws SQLException, IDUsuarioNoExiste {
		this.isasiUsuarios.modificarDatosAcceso(datos);
	}

	/* (non-Javadoc)
	 * @see ifachada.IFachadaUsuarios#identificarse(java.lang.String)
	 */
	@Override
	public Usuario identificarse(TransferUsuario usu)throws SQLException, IDUsuarioNoExiste {
		return this.isasiUsuarios.identificarse(usu);
	}

	@Override
	public void cerrarSesion() throws SQLException {
		this.isasiUsuarios.cerrarSesion();
		
	} 
	@Override
	public void existeId(String id) throws SQLException, IDUsuarioDuplicado {
		 this.isasiUsuarios.existeId(id);
	}
	@Override
	public void altaEmpleado(Empleado e) throws SQLException, IDUsuarioDuplicado {
		this.isasiUsuarios.altaEmpleado(e);
	}
	@Override
	public void altaEncargado(Encargado e) throws SQLException, IDUsuarioDuplicado {
		this.isasiUsuarios.altaEncargado(e);
		
	}
	
	@Override
	public void darDeBajaA(String nick) throws IDUsuarioNoExiste {
		this.isasiUsuarios.darDeBajaA(nick);
		
	}
	@Override
	public List<Usuario> consultarUsuarios() throws SQLException {
		return this.isasiUsuarios.consultarUsuarios();
	}
	@Override
	public List<Usuario> filtrarUsu(Usuario u) throws SQLException {
		return this.isasiUsuarios.filtrarUsu(u);
	}



}
