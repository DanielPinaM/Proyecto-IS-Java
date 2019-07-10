package fachadas;

import java.sql.SQLException;
import java.util.List;

import exception.ProductoYaExistente;
import exception.TallaNoValidaException;
import exception.ValorNoExistenteException;
import iFachadas.interfazStock;
import iSASI.IsasiStock;
import modelodeDominio.Producto;
import sasi.SASIStock;

public class fachadaStock implements interfazStock{

	protected IsasiStock isasiStock;
	
	public fachadaStock() {
		this.isasiStock = new SASIStock();
	}
	
	@Override
	public void altaProducto(Producto producto) throws TallaNoValidaException, SQLException, ProductoYaExistente{
		isasiStock.altaProducto(producto);
		
	}
	
	public void bajaProducto(String idProducto) throws SQLException {
		isasiStock.bajaProducto(idProducto);
		
	}

	@Override
	public List<Producto> consultarStock() throws SQLException {
		return isasiStock.consultarStock();
	}


	@Override
	public boolean existeId(String id) {
		return isasiStock.existeId(id);
	}

}
