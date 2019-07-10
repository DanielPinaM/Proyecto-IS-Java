package sasi;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import modelodeDominio.Filtro;
import dao.daoCompraVenta;
import exception.CantidadSuperadaException;
import exception.ValorNoExistenteException;
import iDAO.idaoCompraVenta;
import iSASI.isasiCompraVenta;
import modelodeDominio.Producto;
import modelodeDominio.Usuario;
import modelodeDominio.Venta;

public class sasiCompraVenta implements isasiCompraVenta{

	protected idaoCompraVenta idaoCompraVenta;
	
	
	public sasiCompraVenta(){
		this.idaoCompraVenta = new daoCompraVenta();
	}
	@Override
	public void realizarCompra(String idUsuario) throws IOException, SQLException {
		this.idaoCompraVenta.realizarCompra(idUsuario);
	}

	@Override
	public void realizarPedidosProveedor(Producto calzados) throws SQLException, ValorNoExistenteException {
		/*if(this.idaoCompraVenta.existe(idCalzado))
			this.idaoCompraVenta.realizarPedidosProveedor(idCalzado, cantidad);
		*/
		try {
			this.idaoCompraVenta.realizarPedidosProveedor(calzados);
		}catch(ValorNoExistenteException a) {
			throw new ValorNoExistenteException();
		}
	}

	@Override
	public void realizarDevolucion(Producto producto, String idUsuario) throws Exception {
		//if(this.idaoCompraVenta.existe(producto)){
			try {
				this.idaoCompraVenta.realizarDevolucion(producto, idUsuario);
			}catch(Exception e) {
				throw new Exception(e.getMessage());
			}
		//}
		//else return false;
	}

	@Override
	public List<Producto> filtrar(Filtro f) throws SQLException {
		return this.idaoCompraVenta.filtrar(f);
		
	}
	@Override
	public List<Venta> consultarRegistroVentas(String mes, String Año, String dia, String nick) throws IOException, SQLException{
		List<Venta> ventas = this.idaoCompraVenta.consultarRegistroVentas(mes, Año, dia, nick);
		if (!ventas.isEmpty())
			return ventas;
		else{
			//lanza mensaje error
			return ventas;
		}
	}

	@Override
	public int consultarPuntos(String idCliente) throws SQLException {
		return this.idaoCompraVenta.consultarPuntos(idCliente);
		
	}

	@Override
	public void canjearPuntos(String idUsuario) throws SQLException {
		//int totalPuntos = this.consultarPuntos(idUsuario);
			//if (totalPuntos == 0)
				//return -1;
			//else{
				this.idaoCompraVenta.canjearPuntos(idUsuario);
				//double precioFinal = precio - (totalPuntos%10)*0.1;
				//return precioFinal;
			//}
		
	}
	
	public void aceptarPedido(String idUsuario) throws SQLException{
		this.idaoCompraVenta.aceptarPedido(idUsuario);
	}
	
	public List<Producto> consultarCarrito() throws SQLException{
		return this.idaoCompraVenta.consultarCarrito();
	}
	
	public List<Venta> consultarRegistroVentas() throws IOException, SQLException{
		return this.idaoCompraVenta.consultarRegistroVentas();
	}
	
	public List<Producto> consultarPedidos() throws SQLException{

		return this.idaoCompraVenta.consultarPedidos();
	}
	
	@Override
	public void carrito(Producto producto) throws SQLException, CantidadSuperadaException {
		try{
			this.idaoCompraVenta.carrito(producto);
		} catch (CantidadSuperadaException e) {
			throw new CantidadSuperadaException("No se puede añadir mas cantidad de la disponible");
		}
		
	}
	
	public void eliminaCarrito(Producto producto) throws SQLException, CantidadSuperadaException{
		try{
			this.idaoCompraVenta.eliminaCarrito(producto);
		}catch(CantidadSuperadaException e) {
			throw new CantidadSuperadaException(e.getMessage());
		}
	}

}

// AQUI TODA LA LOGICA