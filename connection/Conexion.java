package connection;

import java.io.IOException;
import java.sql.*;

import control.ControlCompraVenta;

public class Conexion {
	
 static Connection cx;
 String url="jdbc:mysql://localhost:3306/naik";
 String user= "root";
 String pass="";

 /**
  * 
  * @return Connection
  */
 public Connection conectar() {
	try { 
		Class.forName("com.mysql.jdbc.Driver");
		cx=DriverManager.getConnection(url, user, pass);
		//System.out.println("Se conectó");
	} 
	catch (ClassNotFoundException | SQLException ex) {
		ex.printStackTrace();
	}
	 return cx;
 }
 /**
  * 
  */
 public void desconectar() {
	 try {
		cx.close();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
 }
 
 /**
  * 
  * @return Connection
  */
 public Connection getCx() {
	 return Conexion.cx;
 }
 
 
 
 public static void main(String[]args) {
	 
	 /*Conexion c = new Conexion();
	 c.conectar();
		Statement sa = null;
		try {
			sa = cx.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//sa.executeUpdate("INSERT INTO producto(idProducto, marca, modelo, talla, precio, cantidad) VALUES ('5','NIKE','AIRPOLLAS', '48.5', '120.25', '100')");
			//sa.executeUpdate("DROP TABLE contacto");
			//sa.executeUpdate("CREATE TABLE pedido (id INT AUTO_INCREMENT, PRIMARY KEY(id), nombre VARCHAR(20), apellidos VARCHAR(20), telefono VARCHAR(20))");
			//ResultSet rs = sa.executeQuery("SELECT * FROM contacto"); 
			/*while (rs.next())
			{
			   System.out.println("nombre="+rs.getObject("nombre")+
			      ", apellidos="+rs.getObject("apellidos")+
			      ", telefono="+rs.getObject("telefono"));
			   String apellidos = (String) rs.getObject("apellidos");
			   int nombre = (int) rs.getObject("id");
			   System.out.println(apellidos + " " + nombre);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
	 
	 ControlCompraVenta control = new ControlCompraVenta();
	 
 }
 
}
