/**
 * 
 */
package sasi;

import java.sql.SQLException;
import java.util.List;

import modelodeDominio.Empleado;
import modelodeDominio.Encargado;
import modelodeDominio.Usuario;
import dao.DAOUsuariosImp;
import exception.IDUsuarioDuplicado;
import exception.IDUsuarioNoExiste;
import iDAO.IdaoUsuarios;
import iSASI.IsasiUsuarios;
import transfer.TransferUsuario;

/**
 * @author Naik
 *
 */
public class SASIUsuariosImp implements IsasiUsuarios {

	
	protected IdaoUsuarios idaoUsuarios;
	
	public SASIUsuariosImp(){
		this.idaoUsuarios = new DAOUsuariosImp();
	}
	/* (non-Javadoc)
	 * @see isasi.ISASIUsuarios#darseAlta(transfer.TransferUsuario)
	 */
	@Override
	public void registrarse(TransferUsuario usu) throws SQLException, IDUsuarioDuplicado {
		this.idaoUsuarios.registrarse(usu);
	}

	/* (non-Javadoc)
	 * @see isasi.ISASIUsuarios#darseBaja(transfer.TransferUsuario)
	 */
	@Override
	public void darseBaja(TransferUsuario usu) throws Exception {
		try{
			this.idaoUsuarios.darseBaja(usu);
		}catch(Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see isasi.ISASIUsuarios#modificarDatosAcceso(transfer.TransferUsuario)
	 */
	@Override
	public void modificarDatosAcceso(TransferUsuario datos) throws SQLException, IDUsuarioNoExiste {
		this.idaoUsuarios.modificarDatosAcceso(datos);
	}

	/* (non-Javadoc)
	 * @see isasi.ISASIUsuarios#identificarse(transfer.TransferUsuario)
	 */
	@Override
	public Usuario identificarse(TransferUsuario usu) throws SQLException, IDUsuarioNoExiste {
		Usuario u = this.idaoUsuarios.identificarse(usu);
		if(u != null)
			return u;
		else
			throw new IDUsuarioNoExiste("No existe usuario");
	}

	
	@Override
	public void cerrarSesion() throws SQLException {
		this.idaoUsuarios.cerrarSesion();
	}
	@Override
	public void existeId(String id) throws SQLException, IDUsuarioDuplicado {
		if (this.idaoUsuarios.existeIDUsuario(id))
			throw new IDUsuarioDuplicado("Este usuario ya existe");
	}
	@Override
	public void altaEmpleado(Empleado e) throws SQLException, IDUsuarioDuplicado {
		this.idaoUsuarios.altaEmpleado(e);
		
	}
	@Override
	public void altaEncargado(Encargado e) throws SQLException, IDUsuarioDuplicado {
		this.idaoUsuarios.altaEncargado(e);
		
	}
	
	@Override
	public void darDeBajaA(String nick) throws IDUsuarioNoExiste {
		try {
			if(!this.idaoUsuarios.existeIDUsuario(nick)) {
				throw new IDUsuarioNoExiste("No existe el usuario");
			}else {
				this.idaoUsuarios.darDeBajaA(nick);
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}
	@Override
	public List<Usuario> consultarUsuarios() throws SQLException {
		return this.idaoUsuarios.consultarUsuarios();
	}
	@Override
	public List<Usuario> filtrarUsu(Usuario u) throws SQLException {
		return this.idaoUsuarios.filtrarUsu(u);
	}




}
