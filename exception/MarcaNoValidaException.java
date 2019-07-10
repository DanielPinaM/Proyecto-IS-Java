package exception;

public class MarcaNoValidaException extends Exception {
	
	/**
	 * 
	 */
	public MarcaNoValidaException() {
		super("Inserte una de las marcas que ofrece la tienda. (Nike, Adidas, Puma o New Balance)");
	}
}
