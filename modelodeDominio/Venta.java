package modelodeDominio;


public class Venta implements InterfazVenta{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int Año;
	protected String idProducto;
	protected String idUsuario;
	protected double precio;
	protected String modelo;
	protected int cantidad;
	private int mes;
	private int dia;
	private String marca;
	private double talla;
	
	/**
	 * 
	 * @param dia
	 * @param Año
	 * @param mes
	 * @param idProducto
	 * @param idUsuario
	 * @param precio
	 * @param cantidad
	 * @param marca
	 * @param modelo
	 * @param talla
	 */
	public Venta(int dia, int Año, int mes, String idProducto, String idUsuario, double precio, int cantidad, String marca, String modelo, double talla){
		this.setAño(Año);
		this.setMes(mes);
		this.idProducto = idProducto;
		this.idUsuario = idUsuario;
		this.precio = precio;
		this.cantidad = cantidad;
		this.marca = marca;
		this.modelo = modelo;
		this.talla = talla;
		this.dia = dia;
	}
	
	@Override
	public String getIdProducto() {
		return this.idProducto;
	}
	@Override
	public String getIdUsuario() {
		return this.idUsuario;
	}
	@Override
	public double getPrecio() {
		return this.precio;
	}
	@Override
	public String getModelo() {
		return this.modelo;
	}
	
	public int getCantidad(){
		return this.cantidad;
	}

	public int getAño() {
		return Año;
	}
	
	public String getMarca() {
		return this.marca;
	}
	
	public double getTalla() {
		return this.talla;
	}

	public void setAño(int Año) {
		this.Año = Año;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}
	
	public int getDia() {
		return this.dia;
	}

	
}
