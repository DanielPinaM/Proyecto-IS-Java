package iDAO;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import exception.CantidadSuperadaException;
import exception.ValorNoExistenteException;
import modelodeDominio.Filtro;
import modelodeDominio.Producto;
import modelodeDominio.Usuario;
import modelodeDominio.Venta;

public interface idaoCompraVenta {

	/**
	 * 
	 * @param idUsuario a realizar la compra
	 * @throws IOException excepcion
	 * @throws SQLException excepcion
	 */
	public void realizarCompra(String idUsuario) throws IOException, SQLException;
	/**
	 * 
	 * @param idUsuario a canejar los puntos
	 * @throws SQLException excepcion
	 */
	public void canjearPuntos(String idUsuario) throws SQLException;
	/**
	 * 
	 * @param calzado a pedir
	 * @throws SQLException excepcion
	 * @throws ValorNoExistenteException excepcion
	 */
	public void realizarPedidosProveedor(Producto calzado) throws SQLException, ValorNoExistenteException;
	/**
	 * 
	 * @param f filtro para los productos del stock
	 * @return productos filtrados
	 * @throws SQLException excepcion
	 */
	public List<Producto> filtrar(Filtro f) throws SQLException;
	/**
	 * 
	 * @param idCliente para saber sus puntos
	 * @return puntos totales del usuario
	 * @throws SQLException excepcion
	 */
	public int consultarPuntos(String idCliente) throws SQLException;
	/**
	 * 
	 * @param mes
	 * @param Año
	 * @param dia
	 * @param nick
	 * @return ventas filtradas por parametros
	 * @throws IOException excepcion
	 * @throws SQLException excepcion
	 */
	public List<Venta> consultarRegistroVentas(String mes, String Año, String dia, String nick) throws IOException, SQLException;
	
	/**
	 * 
	 * @param idProducto a aceptar pedido
	 * @throws SQLException excepcion
	 */
	public void aceptarPedido(String idProducto) throws SQLException;
	
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
	 * @return productos en carrito
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
	 * @param producto a añadir al carrito
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
