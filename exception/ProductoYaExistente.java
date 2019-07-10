package exception;

public class ProductoYaExistente extends Exception {
	
	/**
	 * 
	 */
	public ProductoYaExistente() {
		super("El producto introducido ya se encuentra en la base de datos");
	}
}
