package control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import exception.CantidadSuperadaException;
import exception.ValorNoExistenteException;
import fachadas.fachadaCompraVenta;
import iFachadas.interfazCompraVenta;
import modelodeDominio.Filtro;
import modelodeDominio.Producto;
import modelodeDominio.Usuario;
import modelodeDominio.Venta;

public class ControlCompraVenta {
	
	protected interfazCompraVenta ICompraVenta;
	protected Usuario usuarioActual;
	
	/**
	 * 
	 */
	public ControlCompraVenta(){
		this.ICompraVenta = new fachadaCompraVenta();
	}
	
	/**
	 * 
	 * @param mes
	 * @param ano
	 * @param dia
	 * @param nick
	 * @return ventas por filtro de paramentros
	 * @throws IOException excepcion
	 * @throws SQLException excepcion
	 */
	// Devuelve el registro de ventas desde el dia, mes, ano hasta ahora
	public List<Venta> consultarRegistroVentas(String mes, String ano, String dia, String nick) throws IOException, SQLException {
		/*String fecha = Integer.toString(mes) + "/" + Integer.toString(ano);
		this.ICompraVenta.consultarRegistroVentas(fecha);*/
		return this.ICompraVenta.consultarRegistroVentas(mes, ano, dia, nick);
	}
	
	/**
	 * 
	 * @param idUsuario
	 * @throws IOException
	 * @throws SQLException
	 */
	public void realizarCompra(String idUsuario) throws IOException, SQLException {
		this.ICompraVenta.realizarCompra(idUsuario);
	}


	/**
	 * 
	 */
	public void pedirPuntos() {
		
	}

	/**
	 * 
	 * @param idCliente a consultar los puntos
	 * @return int puntos totales
	 * @throws SQLException excepcion
	 */
	public int consultarPuntos(String idCliente) throws SQLException {
		return this.ICompraVenta.consultarPuntos(idCliente);
	}

	/**
	 * 
	 */
	
	/**
	 * 
	 * @param idUsuario a canjear los puntos
	 * @throws SQLException excepcion
	 */
	public void canjearPuntos(String idUsuario) throws SQLException {
		this.ICompraVenta.canjearPuntos(idUsuario);
	}
	
	/**
	 * 
	 * @param producto a devolver
	 * @param idUsuario que realiza la devolucion
	 * @throws Exception excepcion
	 */
	public void realizarDevolucion(Producto producto, String idUsuario) throws Exception{
		//Previamente se ha creado el objeto Transfer producto
		this.ICompraVenta.realizarDevolucion(producto, idUsuario);
		
	}
	
	/**
	 * 
	 * @param calzados a pedir
	 * @throws SQLException excepcion
	 * @throws ValorNoExistenteException excepcion
	 */
	public void pedirCalzado(Producto calzados) throws SQLException, ValorNoExistenteException{
		
		this.ICompraVenta.realizarPedidosProveedor(calzados);
	}
	
	/**
	 * 
	 * @param idProducto a aceptar pedido
	 * @throws SQLException excepcion
	 */
	public void aceptarPedido(String idProducto) throws SQLException{
		this.ICompraVenta.aceptarPedido(idProducto);
	}
	
	/**
	 * 
	 * @param producto a añadir al carrito
	 * @throws SQLException excepcion
	 * @throws CantidadSuperadaException excepcion
	 */
	public void carrito(Producto producto) throws SQLException, CantidadSuperadaException{
		this.ICompraVenta.carrito(producto);
	}
	
	/**
	 * 
	 * @param producto a eliminar del carrito
	 * @throws SQLException excepcion
	 * @throws CantidadSuperadaException excepcion
	 */
	public void eliminaCarrito(Producto producto) throws SQLException, CantidadSuperadaException{
		this.ICompraVenta.eliminaCarrito(producto);
	}
	
	/**
	 * 
	 * @param f filtro para productos
	 * @return productos filtrados por f
	 * @throws SQLException excepcion
	 */
	public List<Producto> filtrar(Filtro f) throws SQLException{
		//Filtro filtro = this.tipoFiltrado();
		return this.ICompraVenta.filtrar(f);
	}
	/**
	 * 
	 * @return productos en el carrito
	 * @throws SQLException excepcion
	 */
	public List<Producto> consultarCarrito() throws SQLException{
		return this.ICompraVenta.consultarCarrito();
	}
	
	/**
	 * 
	 * @return ventas totales 
	 * @throws IOException excepcion
	 * @throws SQLException excepcion
	 */
	public List<Venta> consultarRegistroVentas() throws IOException, SQLException{
		return this.ICompraVenta.consultarRegistroVentas();
	}
	
	/**
	 * 
	 * @return pedidos pendientes
	 * @throws SQLException excepcion
	 */
	public List<Producto> consultarPedidos() throws SQLException{
		return this.ICompraVenta.consultarPedidos();
	}
	
}