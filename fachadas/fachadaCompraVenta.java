package fachadas;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import exception.CantidadSuperadaException;
import exception.ValorNoExistenteException;
import iFachadas.interfazCompraVenta;
import iSASI.isasiCompraVenta;
import modelodeDominio.Filtro;
import modelodeDominio.Producto;
import modelodeDominio.Venta;
import sasi.sasiCompraVenta;


public class fachadaCompraVenta implements interfazCompraVenta {

	protected isasiCompraVenta ISASICompraVenta;
	
	public fachadaCompraVenta(){
		this.ISASICompraVenta = new sasiCompraVenta();
	}

	@Override
	public void realizarPedidosProveedor(Producto calzados) throws SQLException, ValorNoExistenteException {
		this.ISASICompraVenta.realizarPedidosProveedor(calzados);
	}

	@Override
	public void realizarDevolucion(Producto producto, String idUsuario) throws Exception {
		this.ISASICompraVenta.realizarDevolucion(producto, idUsuario);	
	}

	@Override
	public List<Producto> filtrar(Filtro f) throws SQLException {
		return this.ISASICompraVenta.filtrar(f);
	}

	@Override
	public List<Venta> consultarRegistroVentas(String mes, String Año, String dia, String nick) throws IOException, SQLException {
		return this.ISASICompraVenta.consultarRegistroVentas(mes, Año, dia, nick);
		
	}

	@Override
	public int consultarPuntos(String idCliente) throws SQLException {
		return this.ISASICompraVenta.consultarPuntos(idCliente);
		
	}

	@Override
	public void realizarCompra(String idUsuario) throws IOException, SQLException {
		this.ISASICompraVenta.realizarCompra(idUsuario);
		
	}

	@Override
	public void canjearPuntos(String idUsuario) throws SQLException {
		this.ISASICompraVenta.canjearPuntos(idUsuario);
		//Notificar a la gui
		
	}
	
	public void aceptarPedido(String idUsuario) throws SQLException{
		this.ISASICompraVenta.aceptarPedido(idUsuario);
	}
	
	public List<Producto> consultarCarrito() throws SQLException{
		return this.ISASICompraVenta.consultarCarrito();
	}
	
	public List<Venta> consultarRegistroVentas() throws IOException, SQLException{
		return this.ISASICompraVenta.consultarRegistroVentas();
	}
	
	public List<Producto> consultarPedidos() throws SQLException{
		return this.ISASICompraVenta.consultarPedidos();
	}

	@Override
	public void carrito(Producto producto) throws SQLException, CantidadSuperadaException {
		this.ISASICompraVenta.carrito(producto);
		
	}
	
	public void eliminaCarrito(Producto producto) throws SQLException, CantidadSuperadaException{
		this.ISASICompraVenta.eliminaCarrito(producto);
	}

}
