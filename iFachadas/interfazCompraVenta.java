package iFachadas;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import exception.CantidadSuperadaException;
import exception.ValorNoExistenteException;
import modelodeDominio.Filtro;
import modelodeDominio.Producto;
import modelodeDominio.Usuario;
import modelodeDominio.Venta;

public interface interfazCompraVenta {

	/**
	 * 
	 * @param usuarioActual Sera el usuario actual
	 * @throws SQLException exception
	 */
	public void canjearPuntos(String usuarioActual) throws SQLException;
	/**
	 * 
	 * @param calzados  calzado a realizar pedido
	 * @throws SQLException exception
	 * @throws ValorNoExistenteException excepcion
	 */
	public void realizarPedidosProveedor(Producto calzados) throws SQLException, ValorNoExistenteException;
	/**
	 * 
	 * @param idCliente nick del cliente
	 * @return int puntos totales
	 * @throws SQLException excepcion
	 */
	
	public int consultarPuntos(String idCliente) throws SQLException;
	/**
	 * 
	 * @param producto producto a devolver
	 * @param idUsuario nick del usuario que devuelve
	 * @throws SQLException excepcion
	 * @throws Exception excepcion
	 * 
	 */
	public void realizarDevolucion(Producto producto, String idUsuario) throws SQLException, Exception;
	
	/**
	 * 
	 * @param f filtro con los parametros deseados
	 * @return productos devuelve la lista de productos filtrados
	 * @throws SQLException excepcion
	 */
	public List<Producto> filtrar(Filtro f) throws SQLException;
	
	/**
	 * 
	 * @param idUsuario nick del usuario que compra
	 * @throws IOException excepcion
	 * @throws SQLException excepcion
	 */
	public void realizarCompra(String idUsuario) throws IOException, SQLException;
	
	/**
	 * 
	 * @param mes mes filtrado
	 * @param Año Año filtrado
	 * @param dia dia filtrado
	 * @param nick nick filtrado
	 * @return ventas ventas filtrado
	 * @throws IOException excepcion
	 * @throws SQLException excepcion
	 */
	public List<Venta> consultarRegistroVentas(String mes, String Año, String dia, String nick) throws IOException, SQLException;
	
	/**
	 * 
	 * @param idProducto producto aceptado
	 * @throws SQLException excpecion
	 */
	public void aceptarPedido(String idProducto) throws SQLException;
	
	/**
	 * 
	 * @return productos carrito en el momento
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
	 * @return productos pedidos
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
