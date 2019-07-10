package exception;

public class TallaNoValidaException extends Exception {
	
	/**
	 * 
	 */
	public TallaNoValidaException() {
		super("Introduzca una talla mayor que 0 y menor que 55");
	}
}
