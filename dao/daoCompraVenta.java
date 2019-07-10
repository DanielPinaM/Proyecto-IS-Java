package dao;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import connection.Conexion;
import exception.CantidadSuperadaException;
import exception.ValorNoExistenteException;
import iDAO.idaoCompraVenta;
import modelodeDominio.Filtro;
import modelodeDominio.Producto;
import modelodeDominio.Venta;

public class daoCompraVenta implements idaoCompraVenta {
	
	protected static Conexion conexion;
	
	/**
	 * 
	 */
	public daoCompraVenta(){
		this.conexion = new Conexion();
		this.conexion.conectar();
	}
	
	@Override
	public void realizarCompra(String idUsuario) throws IOException, SQLException {
		Statement sa = null;
		try {
			sa = this.conexion.getCx().createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		ResultSet rs = sa.executeQuery("SELECT * FROM carrito"); 
		while(rs.next()){
			sa = this.conexion.getCx().createStatement();
			ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE idProducto = " + rs.getObject("idProducto"));
			rt.next();
			int cantidad = (int) rt.getObject("cantidad") - (int) rs.getObject("cantidad"); 
			int cantidadAComprar = (int) rs.getObject("cantidad");
			
			//Actualiza Stock
			sa = this.conexion.getCx().createStatement();
			sa.executeUpdate("UPDATE producto SET Cantidad = " +cantidad + " WHERE idProducto = " + rs.getObject("idProducto"));
			
			//Actualiza Puntos de usuario
			sa = this.conexion.getCx().createStatement();
			ResultSet ri = sa.executeQuery("SELECT * FROM usuario WHERE Nick = " + "'"+idUsuario+"'");
			ri.next();
			int puntos = (int) ri.getObject("puntos") + 2 * cantidadAComprar;
			sa = this.conexion.getCx().createStatement();
			sa.executeUpdate("UPDATE usuario SET puntos =" + puntos + " WHERE Nick = " + "'"+idUsuario+"'");
			
			Calendar c = new GregorianCalendar();
			int mes = c.get(Calendar.MONTH) + 1;
			int Año = c.get(Calendar.YEAR);	
			int dia = c.get(Calendar.DAY_OF_MONTH);
			
			sa = this.conexion.getCx().createStatement();
			ResultSet ro = sa.executeQuery("SELECT * FROM ventas WHERE Nick = " + "'"+idUsuario+"'" + " AND idProducto =" + rs.getObject("idProducto") + " AND mes =" + mes + " AND Año =" + Año + " AND dia =" + dia);
			if(ro.next()){ // Si estoy comprando el mismo dia el mismo producto
				sa = this.conexion.getCx().createStatement();
				int cant = (int) ro.getObject("cantidad") + (int) rs.getObject("cantidad");
				double precio = ((Number) rs.getObject("precio")).doubleValue() * cant;
				sa.executeUpdate("UPDATE ventas SET cantidad = " + cant + " WHERE Nick = " + "'"+idUsuario+"'" + " AND idProducto =" + rs.getObject("idProducto") + " AND mes =" + mes + " AND Año =" + Año + " AND dia =" + dia);
				sa = this.conexion.getCx().createStatement();
				sa.executeUpdate("UPDATE ventas SET precio = " + precio + " WHERE Nick = " + "'"+idUsuario+"'" + " AND idProducto =" + rs.getObject("idProducto") + " AND mes =" + mes + " AND Año =" + Año + " AND dia =" + dia);
			}
			else{ // Si estoy comprando otro dia el mismo producto
				sa = this.conexion.getCx().createStatement();
				//Insertar una nueva linea
				int cant = (int) rs.getObject("cantidad");
				double precio = ((Number) rs.getObject("precio")).doubleValue() * cant;
						
				sa.executeUpdate("INSERT INTO ventas(Año, cantidad, idProducto, Nick, Mes, Precio, Dia, Marca, Modelo, Talla) VALUES( '" + Año + "','" + rs.getObject("cantidad") + "','" + rs.getObject("idProducto") + "','" +idUsuario+ "','" + mes + "','" + precio + "','" + dia + "','" + rs.getObject("marca") + "','" + rs.getObject("modelo") + "','" + rs.getObject("talla")+ "')");
			}
		}
		rs.close();
		sa = this.conexion.getCx().createStatement();
		sa.executeUpdate("DELETE FROM carrito");
	}

	
	@Override
	public void canjearPuntos(String idUsuario) throws SQLException {
		Statement sa = null;
		try {
			sa = this.conexion.getCx().createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		sa.executeUpdate("UPDATE usuario SET puntos = " + 0 + " WHERE nick = " + "'"+idUsuario+"'");
	}

	@Override
	public void realizarPedidosProveedor(Producto calzado) throws SQLException, ValorNoExistenteException {
		Statement sa = null;
		Statement so = null;
		try {
			sa = this.conexion.getCx().createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(calzado.getId().equals("")){
			ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+calzado.getMarca()+"'" + " AND modelo =" + "'"+calzado.getModelo()+"'" + " AND talla = " + "'"+calzado.getTalla()+"'"); 
			if(!rt.next()){
				throw new ValorNoExistenteException();
			}
			
			String idProducto = (String) rt.getObject("idProducto");
			
			ResultSet rs = sa.executeQuery("SELECT * FROM pedido WHERE marca = " + "'"+calzado.getMarca()+"'" + " AND modelo =" + "'"+calzado.getModelo()+"'" + " AND talla = " + "'"+calzado.getTalla()+"'"); 
			if (!rs.next()) {
				sa.executeUpdate("INSERT INTO pedido(idProducto, marca, modelo, talla, cantidad) VALUES( '" + idProducto + "','" + calzado.getMarca() + "','" + calzado.getModelo() + "','" +calzado.getTalla()+ "','" + calzado.getCantidad() + "')");
				//sa = this.conexion.getCx().createStatement();
				//sa.executeUpdate("UPDATE pedido SET Cantidad = " + calzado.getCantidad() + " WHERE marca = " + "'"+calzado.getMarca()+"'" + " AND modelo =" + "'"+calzado.getModelo()+"'" + " AND talla = " + "'"+calzado.getTalla()+"'");
			}
			else {
				int cantidad = (int) rs.getObject("cantidad");
				cantidad += calzado.getCantidad();
				so = this.conexion.getCx().createStatement();
				so.executeUpdate("UPDATE pedido SET Cantidad = " + cantidad + " WHERE marca = " + "'"+calzado.getMarca()+"'" + " AND modelo =" + "'"+calzado.getModelo()+"'" + " AND talla = " + "'"+calzado.getTalla()+"'");
			}
		}
		else{
			ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE idProducto = " + "'"+calzado.getId()+"'"); 
			if(!rt.next()){
				throw new ValorNoExistenteException();
			}
			String marca = (String) rt.getObject("marca");
			String modelo = (String) rt.getObject("modelo");
			double Talla = ((Number) rt.getObject("talla")).doubleValue();
			
			ResultSet rs = sa.executeQuery("SELECT * FROM pedido WHERE idProducto = " + "'"+calzado.getId()+"'"); 
			if (!rs.next()) {
				sa.executeUpdate("INSERT INTO pedido(idProducto, marca, modelo, talla, cantidad) VALUES( '" + calzado.getId() + "','" + marca + "','" + modelo + "','" +Talla+ "','" + calzado.getCantidad() + "')");
				//sa.executeUpdate("INSERT INTO pedido SELECT idProducto, marca, modelo, cantidad, talla FROM producto WHERE marca =" + "'"+marca+"'" + " AND modelo =" + "'"+modelo+"'" + " AND talla = " + "'"+Talla+"'" + " AND cantidad = " + "'"+calzado.getCantidad()+"'" + " AND idProducto = " + "'"+calzado.getId()+"'");
				//sa = this.conexion.getCx().createStatement();
				//sa.executeUpdate("UPDATE pedido SET Cantidad = " + calzado.getCantidad() + " WHERE marca = " + "'"+calzado.getMarca()+"'" + " AND modelo =" + "'"+calzado.getModelo()+"'" + " AND talla = " + "'"+calzado.getTalla()+"'");
			}
			else {
				int cantidad = (int) rs.getObject("cantidad");
				cantidad += calzado.getCantidad();
				so = this.conexion.getCx().createStatement();
				so.executeUpdate("UPDATE pedido SET Cantidad = " + cantidad + " WHERE idProducto = " + "'"+calzado.getId()+"'");
			}
		}
	}
	
	public void carrito(Producto producto) throws SQLException, CantidadSuperadaException {
		Statement sa = null;
		Statement so = null;
		Statement si = null;
		try {
			sa = this.conexion.getCx().createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet rs = sa.executeQuery("SELECT * FROM carrito WHERE marca = " + "'"+producto.getMarca()+"'" + " AND modelo =" + "'"+producto.getModelo()+"'" + " AND talla = " + "'"+producto.getTalla()+"'"); 
		if (!rs.next()) { // Si no esta en el carrito
			si = this.conexion.getCx().createStatement();
			ResultSet rt = si.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+producto.getMarca()+"'" + " AND modelo =" + "'"+producto.getModelo()+"'" + " AND talla = " + "'"+producto.getTalla()+"'");
			rt.next();
			if ((producto.getCantidad() <= (int) rt.getObject("cantidad")) && (int) rt.getObject("cantidad") != 0) {
				//sa.executeUpdate("INSERT INTO carrito SELECT * FROM producto WHERE marca =" + "'"+producto.getMarca()+"'" + " AND modelo =" + "'"+producto.getModelo()+"'" + " AND talla =" + "'"+producto.getTalla()+"'" );
				sa.executeUpdate("INSERT INTO carrito(idProducto, marca, modelo, talla, precio, cantidad) VALUES (" + "'"+producto.getId()+"'" + "," + "'"+producto.getMarca()+"'" + "," + "'"+producto.getModelo()+"'" + "," + "'"+producto.getTalla()+"'" +"," + "'"+producto.getPrecio()+"'" +"," + "'"+producto.getCantidad()+"'" + ")");
				//sa = this.conexion.getCx().createStatement();
				//sa.executeUpdate("UPDATE carrito SET Cantidad = " + producto.getCantidad() + " WHERE marca = " + "'"+producto.getMarca()+"'" + " AND modelo =" + "'"+producto.getModelo()+"'" + " AND talla = " + "'"+producto.getTalla()+"'");
			}else {
				throw new CantidadSuperadaException("No se puede anadir mas cantidad de la disponible");
			}
		}
		else{ // Si ya esta en el carrito
			si = this.conexion.getCx().createStatement();
			ResultSet rt = si.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+producto.getMarca()+"'" + " AND modelo =" + "'"+producto.getModelo()+"'" + " AND talla = " + "'"+producto.getTalla()+"'");
			int cantidad = (int) rs.getObject("cantidad");
			cantidad += producto.getCantidad();
			rt.next();
			if(cantidad <= (int) rt.getObject("cantidad")) {
				so = this.conexion.getCx().createStatement();
				so.executeUpdate("UPDATE carrito SET Cantidad = " + cantidad + " WHERE marca = " + "'"+producto.getMarca()+"'" + " AND modelo =" + "'"+producto.getModelo()+"'" + " AND talla = " + "'"+producto.getTalla()+"'");
			}
			else {
				throw new CantidadSuperadaException("No se puede anadir mas cantidad de la disponible");
			}
		}
		
	}
	
	public void eliminaCarrito(Producto producto) throws SQLException, CantidadSuperadaException {
		Statement sa = null;
		Statement so = null;
		Statement si = null;
		try {
			sa = this.conexion.getCx().createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//ResultSet rs = sa.executeQuery("SELECT * FROM carrito WHERE marca = " + "'"+producto.getMarca()+"'" + " AND modelo =" + "'"+producto.getModelo()+"'" + " AND talla = " + "'"+producto.getTalla()+"'"); 
		//if (!rs.next()) { // Si no esta en el carrito
			//si = this.conexion.getCx().createStatement();
			//ResultSet rt = si.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+producto.getMarca()+"'" + " AND modelo =" + "'"+producto.getModelo()+"'" + " AND talla = " + "'"+producto.getTalla()+"'");
			//rt.next();
			//if ((producto.getCantidad() <= (int) rt.getObject("cantidad")) && (int) rt.getObject("cantidad") != 0) {
				//sa.executeUpdate("INSERT INTO carrito SELECT * FROM producto WHERE marca =" + "'"+producto.getMarca()+"'" + " AND modelo =" + "'"+producto.getModelo()+"'" + " AND talla =" + "'"+producto.getTalla()+"'" );
				//sa.executeUpdate("INSERT INTO carrito(idProducto, marca, modelo, talla, precio, cantidad) VALUES (" + "'"+producto.getId()+"'" + "," + "'"+producto.getMarca()+"'" + "," + "'"+producto.getModelo()+"'" + "," + "'"+producto.getTalla()+"'" +"," + "'"+producto.getPrecio()+"'" +"," + "'"+producto.getCantidad()+"'" + ")");
				//sa = this.conexion.getCx().createStatement();
				//sa.executeUpdate("UPDATE carrito SET Cantidad = " + producto.getCantidad() + " WHERE marca = " + "'"+producto.getMarca()+"'" + " AND modelo =" + "'"+producto.getModelo()+"'" + " AND talla = " + "'"+producto.getTalla()+"'");
			//}else {
				//throw new CantidadSuperadaException("No se puede anadir mas cantidad de la disponible");
			//}
		//}
		//else{ // Si ya esta en el carrito
			si = this.conexion.getCx().createStatement();
			ResultSet rt = si.executeQuery("SELECT * FROM carrito WHERE marca = " + "'"+producto.getMarca()+"'" + " AND modelo =" + "'"+producto.getModelo()+"'" + " AND talla = " + "'"+producto.getTalla()+"'");
			rt.next();
			int cantidad = (int) rt.getObject("cantidad");
			if(producto.getCantidad() < cantidad) {
				cantidad -= producto.getCantidad();
				so = this.conexion.getCx().createStatement();
				so.executeUpdate("UPDATE carrito SET Cantidad = " + cantidad + " WHERE marca = " + "'"+producto.getMarca()+"'" + " AND modelo =" + "'"+producto.getModelo()+"'" + " AND talla = " + "'"+producto.getTalla()+"'");
			}
			else if (producto.getCantidad() == cantidad){
				so = this.conexion.getCx().createStatement();
				so.executeUpdate("DELETE FROM carrito WHERE idProducto = " +  "'"+producto.getId()+"'");
			}
			else {
				throw new CantidadSuperadaException("No se puede eliminar mas cantidad de la actual");
			}
	}

	@Override
	public List<Producto> filtrar(Filtro f) throws SQLException {
		List<Producto> productos = new ArrayList<Producto>();
		
		//sa.executeUpdate("INSERT INTO ventas(Ano, idProducto, idUsuario, Mes, Precio) VALUES ('1997','2','1','6','100')");
		Statement sa = null;
		try {
			sa = this.conexion.getCx().createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		//sa.executeUpdate("INSERT INTO ventas(Ano, idProducto, idUsuario, Mes, Precio) VALUES ('1997','3', '1', '5', '210')");
		
		double talla = 0;
		double precio = 0;
		if(!f.getTalla().equals(""))
			talla = Double.parseDouble(f.getTalla());
		if(!f.getPrecio().equals(""))
			precio = Double.parseDouble(f.getPrecio());
		
		if(!f.getAdidas().equals("")) {
			if(!f.getNike().equals("")) {
				if(!f.getPuma().equals("")) {
					if(!f.getNewBalance().equals("")) {
						if(!f.getModelo().equals("")) {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
						}
						else {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" +" AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" +  " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" ); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" ); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}	
							}
						}
					}
					else {
						if(!f.getModelo().equals("")) {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								
							}
						}
						else {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" +  " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" ); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" ); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}	
							}
						}
					
					} // else newBalance
					
				} // if puma
				
				else {

					if(!f.getNewBalance().equals("")) {
						if(!f.getModelo().equals("")) {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
						}
						else {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" +" AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" +  " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" ); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" ); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}	
							}
						}
					}
					else {
						if(!f.getModelo().equals("")) {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								
							}
						}
						else {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" +  " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" ); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" ); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}	
							}
						}
					
					} // else newBalance
				
				} // else puma
				
			} // if nike
			else {

				if(!f.getPuma().equals("")) {
					if(!f.getNewBalance().equals("")) {
						if(!f.getModelo().equals("")) {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
	
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
						}
						else {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" +" AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" +  " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" ); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}	
							}
						}
					}
					else {
						if(!f.getModelo().equals("")) {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								
							}
						}
						else {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" +  " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" ); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}	
							}
						}
					
					} // else newBalance
					
				} // if puma
				
				else {

					if(!f.getNewBalance().equals("")) {
						if(!f.getModelo().equals("")) {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
						}
						else {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" +" AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" +  " AND precio = " + "'"+precio+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND precio = " + "'"+precio+"'");
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" ); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}	
							}
						}
					}
					else { // Comienzo del else preocupante
						if(!f.getModelo().equals("")) {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'"); 
									
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								
							}
						}
						else {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" +  " AND precio = " + "'"+precio+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getAdidas()+"'" ); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}	
							}
						}
					
					} // else newBalance
				
				} // else puma
			
			} // else nike
		} // if adidas
		else { // comienzo else adidas

			if(!f.getNike().equals("")) {
				if(!f.getPuma().equals("")) {
					if(!f.getNewBalance().equals("")) {
						if(!f.getModelo().equals("")) {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND precio = " + "'"+precio+"'");
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
						}
						else {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" +" AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									 
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND precio = " + "'"+precio+"'");
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" ); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'"); 
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}	
							}
						}
					}
					else { // else newBalance
						if(!f.getModelo().equals("")) {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'");
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								
							}
						}
						else {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND precio = " + "'"+precio+"'");
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" ); 
									sa = this.conexion.getCx().createStatement();
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'"); 
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}	
							}
						}
					
					} // else newBalance
					
				} // if puma
				
				else { // comienza else de puma

					if(!f.getNewBalance().equals("")) {
						if(!f.getModelo().equals("")) {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND precio = " + "'"+precio+"'");
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
						}
						else {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" +" AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND precio = " + "'"+precio+"'");
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" ); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'"); 
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}	
							}
						}
					}
					else { // comienzo else de puma y newBalance
						if(!f.getModelo().equals("")) {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'");
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								
							}
						}
						else {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" + " AND precio = " + "'"+precio+"'");
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									
									ResultSet rt = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNike()+"'" ); 
									
									while(rt.next()) {
										String Idproducto = (String) rt.getObject("idProducto");
										String Marca = (String) rt.getObject("marca");
										String Modelo = (String) rt.getObject("modelo");
										double Talla = ((Number) rt.getObject("talla")).doubleValue();
										double Precio = ((Number) rt.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rt.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}	
							}
						}
					
					} // else newBalance
				
				} // else puma
				
			} // if nike
			else { // comienza else de nike

				if(!f.getPuma().equals("")) {
					if(!f.getNewBalance().equals("")) {
						if(!f.getModelo().equals("")) {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND precio = " + "'"+precio+"'");
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									
	
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
						}
						else {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" +" AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND precio = " + "'"+precio+"'");
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND precio = " + "'"+precio+"'");
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
						
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'"); 
									sa = this.conexion.getCx().createStatement();
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'"); 
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}	
							}
						}
					}
					else { // comienzo del else de adidas, nike y newBalance
						if(!f.getModelo().equals("")) {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									
									
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'");
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									
									
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
								
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								
							}
						}
						else {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									
									
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									
									
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									 
									
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'" + " AND precio = " + "'"+precio+"'");
									
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									
									
									ResultSet ru = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getPuma()+"'"); 
									
									while(ru.next()) {
										String Idproducto = (String) ru.getObject("idProducto");
										String Marca = (String) ru.getObject("marca");
										String Modelo = (String) ru.getObject("modelo");
										double Talla = ((Number) ru.getObject("talla")).doubleValue();
										double Precio = ((Number) ru.getObject("precio")).doubleValue();
										int Cantidad = ((Number) ru.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}	
							}
						}
					
					} // else newBalance
					
				} // if puma
				
				else {

					if(!f.getNewBalance().equals("")) {
						if(!f.getModelo().equals("")) {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
								
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									
								
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'" + " AND precio = " + "'"+precio+"'");
									
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND modelo =" + "'"+f.getModelo()+"'"); 
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
						}
						else {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" +" AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'");
									
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND talla = " + "'"+f.getTalla()+"'"); 
									
									
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'" + " AND precio = " + "'"+precio+"'");
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									
									ResultSet rv = sa.executeQuery("SELECT * FROM producto WHERE marca = " + "'"+f.getNewBalance()+"'"); 
									
									while(rv.next()) {
										String Idproducto = (String) rv.getObject("idProducto");
										String Marca = (String) rv.getObject("marca");
										String Modelo = (String) rv.getObject("modelo");
										double Talla = ((Number) rv.getObject("talla")).doubleValue();
										double Precio = ((Number) rv.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rv.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}	
							}
						}
					}
					else { // Comienzo del else preocupante
						if(!f.getModelo().equals("")) {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE modelo = " + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'"); 
									
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE modelo = " + "'"+f.getModelo()+"'" + " AND talla = " + "'"+talla+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE modelo =" + "'"+f.getModelo()+"'" +  " AND precio = " + "'"+precio+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE modelo =" + "'"+f.getModelo()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								
							}
						}
						else {
							if(!f.getTalla().equals("")) {
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE talla = " + "'"+talla+"'" + " AND precio = " + "'"+precio+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE talla = " + "'"+f.getTalla()+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								
							}
							else {
								
								if(!f.getPrecio().equals("")) {
									ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE precio = " + "'"+precio+"'"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
									
								}
								else { // Ninguno seleccionado
									ResultSet rs = sa.executeQuery("SELECT * FROM producto"); 
									while(rs.next()) {
										String Idproducto = (String) rs.getObject("idProducto");
										String Marca = (String) rs.getObject("marca");
										String Modelo = (String) rs.getObject("modelo");
										double Talla = ((Number) rs.getObject("talla")).doubleValue();
										double Precio = ((Number) rs.getObject("precio")).doubleValue();
										int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
										
										Producto p = new Producto(Idproducto, Marca, Modelo, Talla, Precio, Cantidad);
										productos.add(p);
									}
								}	
							}
						}
					
					} // else newBalance
				
				} // else puma
			
			} // else nike
		
		} // else adidas 
		
		//Devolver la lista de productos
		return productos;
	}

	@Override
	public List<Venta> consultarRegistroVentas(String mes1, String añoEntrante, String dia1, String nick) throws IOException, SQLException {
		List<Venta> ventas = new ArrayList<Venta>();
		
		//sa.executeUpdate("INSERT INTO ventas(Ano, idProducto, idUsuario, Mes, Precio) VALUES ('1997','2','1','6','100')");
		Statement sa = null;
		try {
			sa = this.conexion.getCx().createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		int dia = 0;
		int mes = 0;
		int añoInicio = 0;
		
		if(!dia1.equals("")) {
			dia = Integer.parseInt(dia1);
		}
		if(!mes1.equals("")) {
			mes = Integer.parseInt(mes1);
		}
		if(!añoEntrante.equals("")) {
			añoInicio = Integer.parseInt(añoEntrante);
		}
		
		
		if(!nick.equals("")) {
			if(!dia1.equals("")) {
				if(!mes1.equals("")) {
					if(!añoEntrante.equals("")) {
						ResultSet rs = sa.executeQuery("SELECT * FROM ventas WHERE Nick = "+ "'"+nick+"'" + " AND Dia =" + dia + " AND Mes =" + mes +" AND Año =" + añoInicio); 
						while (rs.next()){
							int Dia = ((Number) rs.getObject("dia")).intValue();
							int Año = ((Number) rs.getObject("Año")).intValue();
							int Mes = ((Number) rs.getObject("mes")).intValue();
							String Idproducto = (String) rs.getObject("idProducto");
							String Idusuario = (String) rs.getObject("nick");
							double Precio = ((Number) rs.getObject("precio")).doubleValue();
							int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
							String marca = (String) rs.getObject("marca");
 							String modelo = (String) rs.getObject("modelo");
							double talla = ((Number) rs.getObject("talla")).doubleValue();
							
							Venta v = new Venta(Dia, Año, Mes, Idproducto, Idusuario, Precio, Cantidad, marca, modelo, talla);
							ventas.add(v);
						  
						}
					}
					else {
						ResultSet rs = sa.executeQuery("SELECT * FROM ventas WHERE Nick = "+ "'"+nick+"'" + " AND Dia =" + dia + " AND Mes =" + mes); 
						while (rs.next()){
							int Dia = ((Number) rs.getObject("dia")).intValue();
							int Año = ((Number) rs.getObject("Año")).intValue();
							int Mes = ((Number) rs.getObject("mes")).intValue();
							String Idproducto = (String) rs.getObject("idProducto");
							String Idusuario = (String) rs.getObject("nick");
							double Precio = ((Number) rs.getObject("precio")).doubleValue();
							int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
							String marca = (String) rs.getObject("marca");
 							String modelo = (String) rs.getObject("modelo");
							double talla = ((Number) rs.getObject("talla")).doubleValue();
							
							Venta v = new Venta(Dia, Año, Mes, Idproducto, Idusuario, Precio, Cantidad, marca, modelo, talla);
							ventas.add(v);
						  
						}
					} // else de Año
				} //if de mes
				else {
					if(!añoEntrante.equals("")) {
						ResultSet rs = sa.executeQuery("SELECT * FROM ventas WHERE Nick = "+ "'"+nick+"'" + " AND Dia =" + dia +" AND Año =" + añoInicio); 
						while (rs.next()){
							int Dia = ((Number) rs.getObject("dia")).intValue();
							int Año = ((Number) rs.getObject("Año")).intValue();
							int Mes = ((Number) rs.getObject("mes")).intValue();
							String Idproducto = (String) rs.getObject("idProducto");
							String Idusuario = (String) rs.getObject("nick");
							double Precio = ((Number) rs.getObject("precio")).doubleValue();
							int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
							String marca = (String) rs.getObject("marca");
 							String modelo = (String) rs.getObject("modelo");
							double talla = ((Number) rs.getObject("talla")).doubleValue();
							
							Venta v = new Venta(Dia, Año, Mes, Idproducto, Idusuario, Precio, Cantidad, marca, modelo, talla);
							ventas.add(v);
						  
						}
					}
					else {
						ResultSet rs = sa.executeQuery("SELECT * FROM ventas WHERE Nick = "+ "'"+nick+"'" + " AND Dia =" + dia); 
						while (rs.next()){
							int Dia = ((Number) rs.getObject("dia")).intValue();
							int Año = ((Number) rs.getObject("Año")).intValue();
							int Mes = ((Number) rs.getObject("mes")).intValue();
							String Idproducto = (String) rs.getObject("idProducto");
							String Idusuario = (String) rs.getObject("nick");
							double Precio = ((Number) rs.getObject("precio")).doubleValue();
							int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
							String marca = (String) rs.getObject("marca");
 							String modelo = (String) rs.getObject("modelo");
							double talla = ((Number) rs.getObject("talla")).doubleValue();
							
							Venta v = new Venta(Dia, Año, Mes, Idproducto, Idusuario, Precio, Cantidad, marca, modelo, talla);
							ventas.add(v);
						  
						}
					} // else de Año
				
				} // else de mes
			} // if de dia
			else {
				if(!mes1.equals("")) {
					if(!añoEntrante.equals("")) {
						ResultSet rs = sa.executeQuery("SELECT * FROM ventas WHERE Nick = "+ "'"+nick+"'" + " AND Mes =" + mes +" AND Año =" + añoInicio); 
						while (rs.next()){
							int Dia = ((Number) rs.getObject("dia")).intValue();
							int Año = ((Number) rs.getObject("Año")).intValue();
							int Mes = ((Number) rs.getObject("mes")).intValue();
							String Idproducto = (String) rs.getObject("idProducto");
							String Idusuario = (String) rs.getObject("nick");
							double Precio = ((Number) rs.getObject("precio")).doubleValue();
							int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
							String marca = (String) rs.getObject("marca");
 							String modelo = (String) rs.getObject("modelo");
							double talla = ((Number) rs.getObject("talla")).doubleValue();
							
							Venta v = new Venta(Dia, Año, Mes, Idproducto, Idusuario, Precio, Cantidad, marca, modelo, talla);
							ventas.add(v);
						  
						}
					}
					else {
						ResultSet rs = sa.executeQuery("SELECT * FROM ventas WHERE Nick = "+ "'"+nick+"'" + " AND Mes =" + mes); 
						while (rs.next()){
							int Dia = ((Number) rs.getObject("dia")).intValue();
							int Año = ((Number) rs.getObject("Año")).intValue();
							int Mes = ((Number) rs.getObject("mes")).intValue();
							String Idproducto = (String) rs.getObject("idProducto");
							String Idusuario = (String) rs.getObject("nick");
							double Precio = ((Number) rs.getObject("precio")).doubleValue();
							int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
							String marca = (String) rs.getObject("marca");
 							String modelo = (String) rs.getObject("modelo");
							double talla = ((Number) rs.getObject("talla")).doubleValue();
							
							Venta v = new Venta(Dia, Año, Mes, Idproducto, Idusuario, Precio, Cantidad, marca, modelo, talla);
							ventas.add(v);
						  
						}
					} // else de Año
				} //if de mes
				else {
					if(!añoEntrante.equals("")) {
						ResultSet rs = sa.executeQuery("SELECT * FROM ventas WHERE Nick = "+ "'"+nick+"'"  +" AND Año =" + añoInicio); 
						while (rs.next()){
							int Dia = ((Number) rs.getObject("dia")).intValue();
							int Año = ((Number) rs.getObject("Año")).intValue();
							int Mes = ((Number) rs.getObject("mes")).intValue();
							String Idproducto = (String) rs.getObject("idProducto");
							String Idusuario = (String) rs.getObject("nick");
							double Precio = ((Number) rs.getObject("precio")).doubleValue();
							int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
							String marca = (String) rs.getObject("marca");
 							String modelo = (String) rs.getObject("modelo");
							double talla = ((Number) rs.getObject("talla")).doubleValue();
							
							Venta v = new Venta(Dia, Año, Mes, Idproducto, Idusuario, Precio, Cantidad, marca, modelo, talla);
							ventas.add(v);
						  
						}
					}
					else {
						ResultSet rs = sa.executeQuery("SELECT * FROM ventas WHERE Nick = " + "'"+nick+"'"); 
						while (rs.next()){
							int Dia = ((Number) rs.getObject("dia")).intValue();
							int Año = ((Number) rs.getObject("Año")).intValue();
							int Mes = ((Number) rs.getObject("mes")).intValue();
							String Idproducto = (String) rs.getObject("idProducto");
							String Idusuario = (String) rs.getObject("nick");
							double Precio = ((Number) rs.getObject("precio")).doubleValue();
							int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
							String marca = (String) rs.getObject("marca");
 							String modelo = (String) rs.getObject("modelo");
							double talla = ((Number) rs.getObject("talla")).doubleValue();
							
							Venta v = new Venta(Dia, Año, Mes, Idproducto, Idusuario, Precio, Cantidad, marca, modelo, talla);
							ventas.add(v);
						  
						}
					} // else de Año
				
				} // else de mes
			
			} // else de dia
			
		} // if de nick
		else {

			if(!dia1.equals("")) {
				if(!mes1.equals("")) {
					if(!añoEntrante.equals("")) {
						ResultSet rs = sa.executeQuery("SELECT * FROM ventas WHERE Dia =" + dia + " AND Mes =" + mes +" AND Año =" + añoInicio); 
						while (rs.next()){
							int Dia = ((Number) rs.getObject("dia")).intValue();
							int Año = ((Number) rs.getObject("Año")).intValue();
							int Mes = ((Number) rs.getObject("mes")).intValue();
							String Idproducto = (String) rs.getObject("idProducto");
							String Idusuario = (String) rs.getObject("nick");
							double Precio = ((Number) rs.getObject("precio")).doubleValue();
							int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
							String marca = (String) rs.getObject("marca");
 							String modelo = (String) rs.getObject("modelo");
							double talla = ((Number) rs.getObject("talla")).doubleValue();
							
							Venta v = new Venta(Dia, Año, Mes, Idproducto, Idusuario, Precio, Cantidad, marca, modelo, talla);
							ventas.add(v);
						  
						}
					}
					else {
						ResultSet rs = sa.executeQuery("SELECT * FROM ventas WHERE Dia =" + dia + " AND Mes =" + mes); 
						while (rs.next()){
							int Dia = ((Number) rs.getObject("dia")).intValue();
							int Año = ((Number) rs.getObject("Año")).intValue();
							int Mes = ((Number) rs.getObject("mes")).intValue();
							String Idproducto = (String) rs.getObject("idProducto");
							String Idusuario = (String) rs.getObject("nick");
							double Precio = ((Number) rs.getObject("precio")).doubleValue();
							int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
							String marca = (String) rs.getObject("marca");
 							String modelo = (String) rs.getObject("modelo");
							double talla = ((Number) rs.getObject("talla")).doubleValue();
							
							Venta v = new Venta(Dia, Año, Mes, Idproducto, Idusuario, Precio, Cantidad, marca, modelo, talla);
							ventas.add(v);
						  
						}
					} // else de Año
				} //if de mes
				else {
					if(!añoEntrante.equals("")) {
						ResultSet rs = sa.executeQuery("SELECT * FROM ventas WHERE Dia =" + dia +" AND Año =" + añoInicio); 
						while (rs.next()){
							int Dia = ((Number) rs.getObject("dia")).intValue();
							int Año = ((Number) rs.getObject("Año")).intValue();
							int Mes = ((Number) rs.getObject("mes")).intValue();
							String Idproducto = (String) rs.getObject("idProducto");
							String Idusuario = (String) rs.getObject("nick");
							double Precio = ((Number) rs.getObject("precio")).doubleValue();
							int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
							String marca = (String) rs.getObject("marca");
 							String modelo = (String) rs.getObject("modelo");
							double talla = ((Number) rs.getObject("talla")).doubleValue();
							
							Venta v = new Venta(Dia, Año, Mes, Idproducto, Idusuario, Precio, Cantidad, marca, modelo, talla);
							ventas.add(v);
						  
						}
					}
					else {
						ResultSet rs = sa.executeQuery("SELECT * FROM ventas WHERE Dia =" + dia); 
						while (rs.next()){
							int Dia = ((Number) rs.getObject("dia")).intValue();
							int Año = ((Number) rs.getObject("Año")).intValue();
							int Mes = ((Number) rs.getObject("mes")).intValue();
							String Idproducto = (String) rs.getObject("idProducto");
							String Idusuario = (String) rs.getObject("nick");
							double Precio = ((Number) rs.getObject("precio")).doubleValue();
							int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
							String marca = (String) rs.getObject("marca");
 							String modelo = (String) rs.getObject("modelo");
							double talla = ((Number) rs.getObject("talla")).doubleValue();
							
							Venta v = new Venta(Dia, Año, Mes, Idproducto, Idusuario, Precio, Cantidad, marca, modelo, talla);
							ventas.add(v);
						  
						}
					} // else de Año
				
				} // else de mes
			} // if de dia
			else {
				if(!mes1.equals("")) {
					if(!añoEntrante.equals("")) {
						ResultSet rs = sa.executeQuery("SELECT * FROM ventas WHERE Mes =" + mes +" AND Año =" + añoInicio); 
						while (rs.next()){
							int Dia = ((Number) rs.getObject("dia")).intValue();
							int Año = ((Number) rs.getObject("Año")).intValue();
							int Mes = ((Number) rs.getObject("mes")).intValue();
							String Idproducto = (String) rs.getObject("idProducto");
							String Idusuario = (String) rs.getObject("nick");
							double Precio = ((Number) rs.getObject("precio")).doubleValue();
							int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
							String marca = (String) rs.getObject("marca");
 							String modelo = (String) rs.getObject("modelo");
							double talla = ((Number) rs.getObject("talla")).doubleValue();
							
							Venta v = new Venta(Dia, Año, Mes, Idproducto, Idusuario, Precio, Cantidad, marca, modelo, talla);
							ventas.add(v);
						  
						}
					}
					else {
						ResultSet rs = sa.executeQuery("SELECT * FROM ventas WHERE Mes =" + mes); 
						while (rs.next()){
							int Dia = ((Number) rs.getObject("dia")).intValue();
							int Año = ((Number) rs.getObject("Año")).intValue();
							int Mes = ((Number) rs.getObject("mes")).intValue();
							String Idproducto = (String) rs.getObject("idProducto");
							String Idusuario = (String) rs.getObject("nick");
							double Precio = ((Number) rs.getObject("precio")).doubleValue();
							int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
							String marca = (String) rs.getObject("marca");
 							String modelo = (String) rs.getObject("modelo");
							double talla = ((Number) rs.getObject("talla")).doubleValue();
							
							Venta v = new Venta(Dia, Año, Mes, Idproducto, Idusuario, Precio, Cantidad, marca, modelo, talla);
							ventas.add(v);
						  
						}
					} // else de Año
				} //if de mes
				else {
					if(!añoEntrante.equals("")) {
						ResultSet rs = sa.executeQuery("SELECT * FROM ventas WHERE Año =" + añoInicio); 
						while (rs.next()){
							int Dia = ((Number) rs.getObject("dia")).intValue();
							int Año = ((Number) rs.getObject("Año")).intValue();
							int Mes = ((Number) rs.getObject("mes")).intValue();
							String Idproducto = (String) rs.getObject("idProducto");
							String Idusuario = (String) rs.getObject("nick");
							double Precio = ((Number) rs.getObject("precio")).doubleValue();
							int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
							String marca = (String) rs.getObject("marca");
 							String modelo = (String) rs.getObject("modelo");
							double talla = ((Number) rs.getObject("talla")).doubleValue();
							
							Venta v = new Venta(Dia, Año, Mes, Idproducto, Idusuario, Precio, Cantidad, marca, modelo, talla);
							ventas.add(v);
						  
						}
					}
					else { //Muestra todo
						
						ventas = this.consultarRegistroVentas();
						
					} // else de Año
				
				} // else de mes
			
			} // else de dia
		
		} // else de nick
		
	return ventas;
	}

	@Override
	public int consultarPuntos(String nick) throws SQLException {
		Statement sa = null;
		try {
			sa = this.conexion.getCx().createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		//sa.executeUpdate("INSERT INTO ventas(Año, idProducto, idUsuario, Mes, Precio) VALUES ('1997','3', '1', '5', '210')");
		ResultSet rs = sa.executeQuery("SELECT * FROM usuario WHERE nick =" + "'"+nick+"'"); 
		if (rs.next()) {
		   int puntos = ((Number) rs.getObject("puntos")).intValue();
		   return puntos;
		}
		rs.close();
		
		return 0;	
	}


	@Override
	public void aceptarPedido(String idUsuario) throws SQLException {
		Statement sa = null;
		Statement se = null;
		Statement si = null;
		try {
			sa = this.conexion.getCx().createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		//sa.executeUpdate("INSERT INTO ventas(Año, idProducto, idUsuario, Mes, Precio) VALUES ('1997','3', '1', '5', '210')");
		ResultSet rs = sa.executeQuery("SELECT * FROM pedido WHERE idProducto = " + idUsuario); 
		while (rs.next())
		{
			int cantidad = (int) rs.getObject("cantidad");
			se = this.conexion.getCx().createStatement();
			ResultSet recorre = se.executeQuery("SELECT * FROM producto WHERE idProducto = " + rs.getObject("idproducto"));
			
			if(recorre.next()) { // Si el producto se encuentro aun en productos
				cantidad += (int) recorre.getObject("cantidad");
				si = this.conexion.getCx().createStatement();
				si.executeUpdate("UPDATE producto SET Cantidad = " + cantidad + " WHERE idProducto = " + rs.getObject("idproducto"));
				recorre.close();
			}
			sa = this.conexion.getCx().createStatement();
			sa.executeUpdate("DELETE FROM pedido WHERE idProducto = " + rs.getObject("idproducto"));
		}
		rs.close();
	}
	
	public void realizarDevolucion(Producto producto, String nick) throws Exception{
		Statement sa = null;
		Statement se = null;
		try {
			sa = this.conexion.getCx().createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet rs = sa.executeQuery("SELECT * FROM producto WHERE marca =" + "'"+producto.getMarca()+"'" + " AND modelo =" + "'"+producto.getModelo()+"'" + " AND talla = " + "'"+producto.getTalla()+"'");
		String id = "";
		if(rs.next())
			id = (String) rs.getObject("idProducto");
		else
			// No existe el producto
			throw new Exception("No existe el producto seleccionado");
		
		se = this.conexion.getCx().createStatement();
		ResultSet rt = se.executeQuery("SELECT * FROM ventas WHERE idProducto =" + "'"+id+"'" + " AND nick =" + "'"+nick+"'");
		
		if(rt.next()) {
			int cantidad_venta = (int) rt.getObject("cantidad");
			if(producto.getCantidad() < cantidad_venta) { // Devolvemos menos de lo que compramos
				sa = this.conexion.getCx().createStatement();
				int total = cantidad_venta - producto.getCantidad();
				sa.executeUpdate("UPDATE ventas SET Cantidad = " + total + " WHERE idProducto =" + "'"+id+"'" + " AND nick =" + "'"+nick+"'" ); 
				
				se = this.conexion.getCx().createStatement();
				ResultSet ri = se.executeQuery("SELECT * FROM producto WHERE marca =" + "'"+producto.getMarca()+"'" + " AND modelo =" + "'"+producto.getModelo()+"'" + " AND talla = " + "'"+producto.getTalla()+"'");
				ri.next();
				int cantidad_productos = (int) ri.getObject("cantidad");
				total = cantidad_productos + producto.getCantidad();
				
				sa = this.conexion.getCx().createStatement();
				sa.executeUpdate("UPDATE producto SET Cantidad = " + total + " WHERE marca =" + "'"+producto.getMarca()+"'" + " AND modelo =" + "'"+producto.getModelo()+"'" + " AND talla = " + "'"+producto.getTalla()+"'"); 
		
			}
			else if(producto.getCantidad() == cantidad_venta) { //Devolvemos todo
				sa = this.conexion.getCx().createStatement();
				sa.executeUpdate("DELETE FROM ventas WHERE idProducto =" + "'"+id+"'" + " AND nick =" + "'"+nick+"'" ); 
				
				se = this.conexion.getCx().createStatement();
				ResultSet ri = se.executeQuery("SELECT * FROM producto WHERE marca =" + "'"+producto.getMarca()+"'" + " AND modelo =" + "'"+producto.getModelo()+"'" + " AND talla = " + "'"+producto.getTalla()+"'");
				ri.next();
				int cantidad_productos = (int) ri.getObject("cantidad");
				int total = cantidad_productos + producto.getCantidad();
				
				sa = this.conexion.getCx().createStatement();
				sa.executeUpdate("UPDATE producto SET Cantidad = " + total + " WHERE marca =" + "'"+producto.getMarca()+"'" + " AND modelo =" + "'"+producto.getModelo()+"'" + " AND talla = " + "'"+producto.getTalla()+"'"); 
		
			}
			else {
				// ERROR NO SE DEVUELVEN MAS DE LOS QUE SE COMPRO
				throw new Exception("No se pueden devolver mas cantidad de la que ha comprado");
			}
			
		}
		else
			//ERROR NO SE VENDIO ESE ARTICULO
			throw new Exception("No se ha registrado ninguna venta del artículo a este usuario");
		
		rs.close();
	}
	
	public List<Producto> consultarCarrito() throws SQLException {
		List<Producto> productos = new ArrayList<Producto>();
		
		Statement sa = null;
		try {
			sa = this.conexion.getCx().createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		ResultSet rs = sa.executeQuery("SELECT * FROM carrito"); 
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
	
	public List<Venta> consultarRegistroVentas() throws IOException, SQLException {
		List<Venta> ventas = new ArrayList<Venta>();
		
		//sa.executeUpdate("INSERT INTO ventas(Año, idProducto, idUsuario, Mes, Precio) VALUES ('1997','2','1','6','100')");
		Statement sa = null;
		try {
			sa = this.conexion.getCx().createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		//sa.executeUpdate("INSERT INTO ventas(Año, idProducto, idUsuario, Mes, Precio) VALUES ('1997','3', '1', '5', '210')");
		ResultSet rs = sa.executeQuery("SELECT * FROM ventas"); 
		while (rs.next()){
			//((Number) rs.getObject(1)).intValue();
			int Dia = ((Number) rs.getObject("dia")).intValue();
			int Año = ((Number) rs.getObject("Año")).intValue();
			int Mes = ((Number) rs.getObject("mes")).intValue();
			String Idproducto = (String) rs.getObject("idProducto");
			String Idusuario = (String) rs.getObject("nick");
			BigDecimal precio =  (BigDecimal) rs.getObject("precio");
			double Precio = precio.doubleValue();
			int Cantidad = ((Number) rs.getObject("cantidad")).intValue();
			String marca = (String) rs.getObject("marca");
			String modelo = (String) rs.getObject("modelo");
			double talla = ((Number) rs.getObject("talla")).doubleValue();
			
			Venta v = new Venta(Dia, Año, Mes, Idproducto, Idusuario, Precio, Cantidad, marca, modelo, talla);
			ventas.add(v);
		  
		}
		rs.close();	
	return ventas;
	}
	
	public List<Producto> consultarPedidos() throws SQLException {
		List<Producto> productos = new ArrayList<Producto>();
		
		Statement sa = null;
		try {
			sa = this.conexion.getCx().createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		ResultSet rs = sa.executeQuery("SELECT * FROM pedido"); 
		while (rs.next())
		{		
		   String id = (String) rs.getObject("idProducto");	
		   String marca = (String) rs.getObject("marca");
		   String modelo = (String) rs.getObject("modelo");
		   BigDecimal talla =  (BigDecimal) rs.getObject("talla");
		   double tallaDef = talla.doubleValue();
		   int cantidad = (int) rs.getObject("cantidad");
		   
		   //System.out.println(id + "  " + marca + "  " + modelo + "  " + String.valueOf(tallaDef) + "  " + String.valueOf(precioDef) + "  " + String.valueOf(cantidad));
		   
		   Producto p = new Producto(id, marca, modelo, tallaDef, 0, cantidad);
		   productos.add(p);
		}
		rs.close();	
		
		//Devolver la lista de productos
		return productos;
	}




	
	//Aqui todo el manejo de datos
	
	
}
