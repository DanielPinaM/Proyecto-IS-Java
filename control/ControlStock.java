package control;



import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import exception.ProductoYaExistente;
import exception.TallaNoValidaException;
import exception.ValorNoExistenteException;
import fachadas.fachadaStock;
import iFachadas.interfazStock;
import modelodeDominio.*;

public class ControlStock {

	
	protected interfazStock IStock;
	protected Usuario usuarioActual;
	
	/**
	 * 
	 */
	public ControlStock(){
		this.IStock = new fachadaStock();
	}
	
	/**
	 * 
	 * @param producto a dar de alta
	 * @throws TallaNoValidaException excepcion
	 * @throws SQLException excepcion
	 * @throws ProductoYaExistente excepcion
 	 */
	public void altaProducto(Producto producto) throws TallaNoValidaException, SQLException, ProductoYaExistente {
		this.IStock.altaProducto(producto);
	}

	
	/**
	 * 
	 * @param idProducto a dar de baja
	 * @throws SQLException excepcion
	 */
	public void bajaProducto(String idProducto) throws SQLException {
		this.IStock.bajaProducto(idProducto);
	}
	
	/**
	 * 
	 * @return productos en stock
	 * @throws IOException excepcion
	 * @throws SQLException excepcion
	 */
	public List<Producto> consultarStock() throws IOException, SQLException {
		return this.IStock.consultarStock();
	}

	/**
	 * 
	 * @param string id del producto
	 * @return boolean true si existe
	 */
	public boolean existeId(String string) {
		return this.IStock.existeId(string);
	}
	

}
