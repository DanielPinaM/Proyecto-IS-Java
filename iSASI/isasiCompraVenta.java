package iSASI;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import exception.CantidadSuperadaException;
import exception.ValorNoExistenteException;
import modelodeDominio.Filtro;
import modelodeDominio.Producto;
import modelodeDominio.Usuario;
import modelodeDominio.Venta;

public interface isasiCompraVenta {
	
	/**
	 * 
	 * @param idUsuario que realiza la compra
	 * @throws IOException excepcion
	 * @throws SQLException excepcion
	 */
	public void realizarCompra(String idUsuario) throws IOException, SQLException;
	/**
	 * 
	 * @param idUsuario sera el usuario actual
	 * @throws SQLException exception
	 */
	public void canjearPuntos(String idUsuario) throws SQLException;
	/**
	 * 
	 * @param calzados que hay que pedir
	 * @throws SQLException excepcion
	 * @throws ValorNoExistenteException excepcion
	 */
	public void realizarPedidosProveedor(Producto calzados) throws SQLException, ValorNoExistenteException;
	/**
	 * 
	 * @param idCliente para consultar sus puntos
	 * @return int sus puntos
	 * @throws SQLException excepcion
	 */
	public int consultarPuntos(String idCliente) throws SQLException;
	/**
	 * 
	 * @param producto a devolver
	 * @param idUsuario que devuelve
	 * @throws SQLException excepcion
	 * @throws Exception excepcion
	 */
	public void realizarDevolucion(Producto producto, String idUsuario) throws SQLException, Exception;
	/**
	 * 
	 * @param f filtro para mostrar lo deseado
	 * @return productos que cumplen el filtro
	 * @throws SQLException excepcion
	 */
	public List<Producto> filtrar(Filtro f) throws SQLException;
	
	/**
	 * 
	 * @param mes 
	 * @param Año
	 * @param dia
	 * @param nick
	 * @return ventas que coincides
	 * @throws IOException excepcion
	 * @throws SQLException excepcion
	 */
	public List<Venta> consultarRegistroVentas(String mes, String Año, String dia, String nick) throws IOException, SQLException;
	/**
	 * 
	 * @param idProducto del que hay que aceptar
	 * @throws SQLException excepcion
	 */ 
	public void aceptarPedido(String idProducto) throws SQLException;
	
	/**
	 * 
	 * @return carrito hasta el momento
	 * @throws SQLException excepcion
	 */
	public List<Producto> consultarCarrito() throws SQLException;
	/**
	 * 
	 * @return ventas totales
	 * @throws IOException excepcion
	 * @throws SQLException excepcion
	 */
	public List<Venta> consultarRegistroVentas() throws IOException, SQLException;
	/**
	 * 
	 * @return pedidos realizados
	 * @throws SQLException excepcion
	 */
	public List<Producto> consultarPedidos() throws SQLException;
	/**
	 * 
	 * @param producto a añadir
	 * @throws SQLException excepcion
	 * @throws CantidadSuperadaException excepcion
	 */
	public void carrito(Producto producto) throws SQLException, CantidadSuperadaException;
	/**
	 * 
	 * @param producto a eliminar
	 * @throws SQLException excepcion
	 * @throws CantidadSuperadaException excepcion
	 */
	public void eliminaCarrito(Producto producto) throws SQLException, CantidadSuperadaException;
	
}
