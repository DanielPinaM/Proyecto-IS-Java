package iDAO;

import java.sql.SQLException;
import java.util.List;

import exception.MarcaNoValidaException;
import exception.ProductoYaExistente;
import exception.TallaNoValidaException;
import exception.ValorNoExistenteException;
import modelodeDominio.Producto;

public interface IdaoStock {

	/**
	 * 
	 * @param producto a dar de alta
	 * @throws TallaNoValidaException excepcion
	 * @throws SQLException excepcion
	 * @throws ProductoYaExistente excepcion
 	 */
	void altaProducto(Producto producto) throws TallaNoValidaException, SQLException, ProductoYaExistente;
	
	/**
	 * 
	 * @param idProducto a dar de baja
	 * @throws SQLException excepcion
	 */
	void bajaProducto(String idProducto) throws SQLException;

	/**
	 * 
	 * @return stock disponible total 
	 * @throws SQLException excepcion
	 */
	List<Producto> consultarStock() throws SQLException;

	/**
	 * 
	 * @param id
	 * @return true si existe un producto
	 */
	boolean existeId(String id);

}
