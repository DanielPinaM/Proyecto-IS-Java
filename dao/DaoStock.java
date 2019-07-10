package dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import connection.Conexion;
import exception.MarcaNoValidaException;
import exception.ProductoYaExistente;
import exception.TallaNoValidaException;
import exception.ValorNoExistenteException;
import iDAO.IdaoStock;
import modelodeDominio.Producto;

public class DaoStock implements IdaoStock {

	protected static Conexion conexion;
	
	public DaoStock(){
		this.conexion = new Conexion();
		this.conexion.conectar();
	}
	
	@Override
	public void altaProducto(Producto p) throws TallaNoValidaException, SQLException, ProductoYaExistente {
		//Genera nuevo producto en BD
		String marcaDada = p.getMarca();
		double tallaDada = p.getTalla();
		
		//if(!marcaDada.equalsIgnoreCase("Nike") && !marcaDada.equalsIgnoreCase("Adidas") && !marcaDada.equalsIgnoreCase("Puma") && !marcaDada.equalsIgnoreCase("New Balance") )
			//throw new MarcaNoValidaException();
		if(tallaDada > 55 || tallaDada < 0) 
			throw new TallaNoValidaException();
		
		Statement sa = null;
		try {
			sa = this.conexion.getCx().createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE idProducto = '" + p.getId() + "'");
		while(rt.next()) {
			sa = this.conexion.getCx().createStatement();
			p.setId();
			rt = sa.executeQuery("SELECT * FROM producto WHERE idProducto = '" + p.getId() + "'");
		}
		
		sa = this.conexion.getCx().createStatement();
		ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = '" + p.getMarca() + "' AND modelo = '" + p.getModelo() + "' AND talla = '" + p.getTalla() + "'" );
		if(rs.next()) 
			throw new ProductoYaExistente();
		
			sa.executeUpdate("INSERT into producto(idProducto, marca, modelo, talla, precio, cantidad) VALUES ('"+p.getId()+"','"+p.getMarca()+"','" +p.getModelo()+"',"+p.getTalla()+"," +p.getPrecio()+ "," + p.getCantidad()+ ")");
				
	}

	public void bajaProducto(String idProducto) throws SQLException {
		//Delete de fila en BD		
		Statement sa = null;
		
		try {
			sa = this.conexion.getCx().createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
			sa.executeUpdate("DELETE FROM carrito WHERE idProducto = '" + idProducto + "'");
			sa = this.conexion.getCx().createStatement();
			sa.executeUpdate("DELETE FROM producto WHERE idProducto = '" + idProducto + "'");
	}

	@Override
	public List<Producto> consultarStock() throws SQLException {
		List<Producto> productos = new ArrayList<Producto>();
		
		Statement sa = null;
		try {
			sa = this.conexion.getCx().createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		ResultSet rs = sa.executeQuery("SELECT * FROM producto"); 
		while (rs.next())
		{	
		   String id = (String) rs.getObject("idProducto");
		   String marca = (String) rs.getObject("marca");
		   String modelo = (String) rs.getObject("modelo");
		   BigDecimal talla =  (BigDecimal) rs.getObject("talla");
		   double tallaDef = talla.doubleValue();
		   BigDecimal precio =  (BigDecimal) rs.getObject("precio");
		   double precioDef = precio.doubleValue();
		   int cantidad = (int) rs.getObject("cantidad");
		   
		   //System.out.println(id + "  " + marca + "  " + modelo + "  " + String.valueOf(tallaDef) + "  " + String.valueOf(precioDef) + "  " + String.valueOf(cantidad));
		   
		   Producto p = new Producto(id, marca, modelo, tallaDef, precioDef, cantidad);
		   productos.add(p);
		}
		rs.close();	
		
		//Devolver la lista de productos
		return productos;
	}


	@Override
	public boolean existeId(String id) {
		Statement sa = null;
		
		try {
			sa = this.conexion.getCx().createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		try {
			ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE idproducto = '" + id  + "'");
			return rs.next();
			
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
