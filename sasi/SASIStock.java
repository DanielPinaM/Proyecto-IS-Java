package sasi;

import java.sql.SQLException;
import java.util.List;

import dao.DaoStock;
import exception.ProductoYaExistente;
import exception.TallaNoValidaException;
import exception.ValorNoExistenteException;
import iDAO.IdaoStock;
import iSASI.IsasiStock;
import modelodeDominio.Producto;

public class SASIStock implements IsasiStock {
	
	protected IdaoStock idaoStock;
	
	public SASIStock() {
		this.idaoStock = new DaoStock();
	}

	@Override
	public void altaProducto(Producto producto) throws TallaNoValidaException, SQLException, ProductoYaExistente {
		try{
			idaoStock.altaProducto(producto);
		}catch (ProductoYaExistente e) {
			throw new ProductoYaExistente();
		}
	}
	
	public void bajaProducto(String idProducto) throws SQLException {
		idaoStock.bajaProducto(idProducto);
	}

	@Override
	public List<Producto> consultarStock() throws SQLException {
		return idaoStock.consultarStock();
	}

	@Override
	public boolean existeId(String id) {
		return idaoStock.existeId(id);
	}


}
