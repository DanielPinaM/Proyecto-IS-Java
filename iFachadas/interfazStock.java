package iFachadas;

import java.sql.SQLException;
import java.util.List;

import exception.ProductoYaExistente;
import exception.TallaNoValidaException;
import exception.ValorNoExistenteException;
import modelodeDominio.Producto;

public interface interfazStock {

	/**
	 * 
	 * @param producto a dar de alta
	 * @throws TallaNoValidaException excepcion
	 * @throws SQLException excepcion
	 * @throws ProductoYaExistente excepcion
	 */
	public void altaProducto(Producto producto) throws TallaNoValidaException, SQLException, ProductoYaExistente;
	
	/**
	 * 
	 * @param idProducto a dar de baja
	 * @throws SQLException expecion
	 */
	public void bajaProducto(String idProducto) throws SQLException;
	
	/**
	 * 
	 * @return productos en stock
	 * @throws SQLException excepcion
	 */
	public List<Producto> consultarStock() throws SQLException;
	
	/**
	 * 
	 * @param string id del producto
	 * @return true si existe
	 */
	public boolean existeId(String string);
	
}
