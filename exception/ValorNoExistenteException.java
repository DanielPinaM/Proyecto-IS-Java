package exception;

public class ValorNoExistenteException extends Exception {
	
	/**
	 * 
	 */
	public ValorNoExistenteException() {
		super("Producto no existente en el stock");
	}
}
